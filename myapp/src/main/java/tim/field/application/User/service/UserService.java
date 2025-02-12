package tim.field.application.User.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import tim.field.application.User.dto.GroupDTO;
import tim.field.application.User.dto.PermissionDTO;
import tim.field.application.User.dto.RegisterUserDTO;
import tim.field.application.User.dto.UserDetailsDTO;
import tim.field.application.User.dto.UserManagementDTO;
import tim.field.application.User.dto.UserResponseDTO;
import tim.field.application.User.mapper.UserMapper;
import tim.field.application.User.model.Group;
import tim.field.application.User.model.GroupInvitationCode;
import tim.field.application.User.model.Permission;
import tim.field.application.User.model.User;
import tim.field.application.User.model.UserGroup;
import tim.field.application.User.model.UserGroupId;
import tim.field.application.User.model.UserPermission;
import tim.field.application.User.model.UserPermissionId;
import tim.field.application.User.repository.GroupInvitationCodeRepository;
import tim.field.application.User.repository.UserGroupRepository;
import tim.field.application.User.repository.UserPermissionRepository;
import tim.field.application.User.repository.UserRepository;
import tim.field.application.security.JwtUtil;
import tim.field.application.util.GlobalResponse;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private JwtUtil jwtUtil ;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GroupService groupService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserPermissionRepository userPermissionRepository;
	
    
    private final MailjetClient mailjetClient;

    @Autowired
    private GroupInvitationCodeRepository groupInvitationCodeRepository;

    @Value("${mailjet.sender.email}")
    private String senderEmail;

    @Value("${mailjet.sender.name}")
    private String senderName;

    public UserService(@Value("${mailjet.api.key}") String apiKey,
                       @Value("${mailjet.api.secret}") String apiSecret) {
        ClientOptions options = ClientOptions.builder()
                .apiKey(apiKey)
                .apiSecretKey(apiSecret)
                .build();

        this.mailjetClient = new MailjetClient(options);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByUsernameWithRelations(String username) {
        return userRepository.findByUsernameWithPermissions(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    public User findByIdWithRelations(Long id) {
        return userRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    // Cadastrar novo usuário
    public UserResponseDTO registerNewUser(RegisterUserDTO registerUserDTO) {
        LOGGER.info("Registrando novo usuário: {}", registerUserDTO.getUsername());
    
        // Verifica se o usuário já existe
        if (userRepository.findByUsernameWithPermissions(registerUserDTO.getUsername()).isPresent()) {
            throw new RuntimeException("O username já está em uso.");
        }
    
        // Verifica se o e-mail já está cadastrado
        if (userRepository.findByEmail(registerUserDTO.getEmail()).isPresent()) {
            throw new RuntimeException("O e-mail fornecido já está cadastrado.");
        }
    
        // Verifica o código de convite, se fornecido
        Group group = null;
        if (registerUserDTO.getInviteCode() != null && !registerUserDTO.getInviteCode().isEmpty()) {
            group = validateInviteCode(registerUserDTO.getInviteCode());
        }
    
        // Converte o RegisterUserDTO para o modelo User
        User user = new User();
        user.setUsername(registerUserDTO.getUsername());
        user.setEmail(registerUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerUserDTO.getPassword())); // Codifica a senha
        user.setFullName(registerUserDTO.getFullName());
        user.setPhoneNumber(registerUserDTO.getPhoneNumber());
        user.setStatus("inativo"); // Define o status inicial como ativo
        user.setCreatedAt(LocalDateTime.now());
        user.setActivationToken(generateToken()); // Gera o token de ativação
        user.setMatricula(registerUserDTO.getMatricula());
    
        // Gera o segredo TOTP e o QR Code
        final GoogleAuthenticatorKey key = new GoogleAuthenticator().createCredentials();
        String issuer = "4Field"; // Nome da aplicação
    
        String qrCodeData = String.format(
            "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
            issuer,
            user.getUsername(),
            key.getKey(),
            issuer
        );
    
        String qrCodeUrl;
        try {
            qrCodeUrl = String.format(
                "https://api.qrserver.com/v1/create-qr-code/?data=%s",
                URLEncoder.encode(qrCodeData, "UTF-8")
            );
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Erro ao codificar URL do QR Code: ", e);
            throw new RuntimeException("Erro ao gerar o QR Code", e);
        }
    
        user.setTwoFactorSecret(key.getKey());
        user.setQrCodeUrl(qrCodeUrl);
    
        // Salva o usuário no banco
        User savedUser = userRepository.save(user);
        LOGGER.info("Usuário salvo com sucesso: {}", savedUser.getUsername());
    
        // Se o código de convite for válido, adiciona o usuário ao grupo relacionado
        if (group != null) {
            LOGGER.info("Associando usuário '{}' ao grupo '{}'.", savedUser.getUsername(), group.getName());
            addUserToGroups(savedUser.getId(), List.of(group.getId()));
        }
    
        sendActivationEmail(savedUser, qrCodeUrl); // Envia e-mail de ativação
    
        return toUserResponseDTO(savedUser); // Converte o modelo para UserResponseDTO
    }

    /*
     * Atualiza os dados do usuário
     */
    
     public UserDetailsDTO updateUser(UserDetailsDTO userToUpdate) {

        User user = findById(userToUpdate.getUserId());
    
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }
    
        // Atualiza somente os campos presentes no DTO
        if (userToUpdate.getFullName() != null) {
            user.setFullName(userToUpdate.getFullName());
        }
    
        if (userToUpdate.getUsername() != null) {
            user.setUsername(userToUpdate.getUsername());
        }
    
        if (userToUpdate.getBio() != null) {
            user.setBio(userToUpdate.getBio());
        }
    
        userRepository.save(user);
    
        return userToUpdate;
    }    

    /**
     * Valida o código de convite e retorna o grupo associado.
     */
    private Group validateInviteCode(String inviteCode) {
        GroupInvitationCode invitationCode = groupInvitationCodeRepository.findByCode(inviteCode)
                .orElseThrow(() -> new RuntimeException("Código de convite inválido."));
    
        // Verifica se o código expirou
        if (invitationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Código de convite expirado.");
        }
    
        LOGGER.info("Código de convite válido para o grupo: {}", invitationCode.getGroup().getName());
    
        return invitationCode.getGroup();
    }     

    // Gerar token de ativação
    private String generateToken() {
        byte[] tokenBytes = new byte[20];
        new java.security.SecureRandom().nextBytes(tokenBytes);
        return java.util.Base64.getEncoder().encodeToString(tokenBytes);
    }

    private void sendActivationEmail(User user, String qrCodeUrl) {
        String subject = "Ativação da conta";
    
        // Gerar o conteúdo HTML do e-mail
        String htmlContent = 
            "<div style='font-family: Arial, sans-serif; background-color: #f3f4f6; color: #1F2F46; text-align: center; padding: 20px;'>"
            + "    <!-- Banner -->"
            + "    <div style='background-color: #2d2741; color: white; padding: 20px; border-radius: 10px; margin-bottom: 20px;'>"
            + "        <h1 style='margin: 10px 0;'>4Field</h1>"
            + "        <p style='font-size: 18px;'>Bem-vindo à ativação de conta!</p>"
            + "    </div>"
            + "    <!-- Saudação -->"
            + "    <p style='font-size: 18px;'>Olá " + user.getFullName() + ",</p>"
            + "    <p>Estamos quase lá! Complete sua ativação seguindo as instruções abaixo.</p>"
            + "    <!-- Tabela para os Cards -->"
            + "    <table style='width: 100%; margin-top: 20px; border-spacing: 20px;'>"
            + "        <tr>"
            + "            <!-- Card Token -->"
            + "            <td style='background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); padding: 20px; text-align: center; vertical-align: top;'>"
            + "                <h3 style='margin-bottom: 10px;'>Token de Ativação</h3>"
            + "                <p style='font-size: 18px; color: white; background-color: #2d2741; padding: 10px; border-radius: 5px;'>"
            +                  user.getActivationToken() + "</p>"
            + "                <p style='font-size: 14px; margin-top: 10px;'>Insira este token na página de confirmação para ativar sua conta.</p>"
            + "            </td>"
            + "            <!-- Card QR Code -->"
            + "            <td style='background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); padding: 20px; text-align: center; vertical-align: top;'>"
            + "                <h3 style='margin-bottom: 10px;'>Configuração 2FA</h3>"
            + "                <img src='" + qrCodeUrl + "' alt='QR Code de configuração 2FA' style='width: 150px; height: 150px; margin-top: 10px;' />"
            + "                <p style='font-size: 14px; margin-top: 10px;'>Use o Microsoft Authenticator para escanear o QR Code e configurar a autenticação em dois fatores.</p>"
            + "            </td>"
            + "        </tr>"
            + "    </table>"
            + "</div>";
    
        // Enviar o e-mail
        sendEmail(user.getEmail(), subject, htmlContent);
    }        
    
    // Validar token de ativação
    public boolean validateActivationToken(String token, HttpServletRequest request) {
        
        Optional<User> userOptional = userRepository.findByActivationToken(token);

        LocalDateTime updatedAt = LocalDateTime.now();

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Obter usuário logado do contexto de segurança
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                String loggedUsername = null;

                if (principal instanceof UserDetails) {
                    loggedUsername = ((UserDetails) principal).getUsername();
                } else if (principal instanceof String) {
                    loggedUsername = (String) principal;
                }

                LOGGER.info("Comparando usuários: loggedUser={} tokenUser={}", loggedUsername, user.getUsername());

                if (loggedUsername != null && !loggedUsername.equals(user.getUsername())) {
                    request.setAttribute("logMessage", "Logged user does not match token user");
                    request.setAttribute("apiMessage", "O usuário logado não corresponde ao token de ativação.");
                    return false;
                }
            } else {
                request.setAttribute("logMessage", "No authenticated user in context");
                request.setAttribute("apiMessage", "Nenhum usuário autenticado no contexto.");
                return false;
            }

            try {
                // IDs das permissões a serem atribuídas
                List<Long> permissionIds = List.of(1L, 2L); // Exemplo: 1L para '2FA_PRIVILEGE', 2L para 'USER_PRIVILEGE'
            
                // Adicionar permissões ao usuário utilizando o método novo
                addPermissionsToUser(user.getId(), permissionIds);
            
                // Agora, validar o token e confirmar o e-mail
                user.setActivationToken(null);
                user.setEmailVerified("sim");
                user.setStatus("ativo");
                user.setUpdatedAt(updatedAt);
                userRepository.save(user);
            
                LOGGER.info("User '{}' activated successfully with permissions: {}", user.getUsername(), permissionIds);
            
                request.setAttribute("logMessage", "Email confirmed and permissions added successfully");
                request.setAttribute("apiMessage", "E-mail confirmado e permissões atribuídas com sucesso.");
                return true;
            
            } catch (RuntimeException e) {
                LOGGER.error("Erro ao atribuir permissões ao usuário '{}': {}", user.getUsername(), e.getMessage());
                request.setAttribute("logMessage", "Error assigning permissions");
                request.setAttribute("apiMessage", "Erro ao atribuir permissões ao usuário.");
                return false;
            }
        }

        LOGGER.warn("Token de ativação inválido: {}", token);
        request.setAttribute("logMessage", "Invalid activation token");
        request.setAttribute("apiMessage", "Token de ativação inválido.");
        return false;
    }

    // Redefinir senha
    public boolean resetPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = generateToken();
            user.setActivationToken(token);
            userRepository.save(user);

            LOGGER.info("Token de redefinição enviado para '{}'.", email);
            sendResetPasswordEmail(user);
            return true;
        }

        LOGGER.warn("Usuário com e-mail '{}' não encontrado.", email);
        return false;
    }

    // Enviar e-mail de redefinição de senha
    private void sendResetPasswordEmail(User user) {
        String subject = "Redefinição de Senha";
        String htmlContent = String.format(
                "<div style='font-family: Arial, sans-serif; color: #1F2F46; text-align: center;'>"
                        + "<h1 style='color: #1F2F46;'>4Field</h1>"
                        + "<h2>Redefinição de Senha</h2>"
                        + "<p style='font-size: 18px;'>Olá %s,</p>"
                        + "<p>Seu token para redefinição de senha é:</p>"
                        + "<h3 style='background-color: #1F2F46; color: white; display: inline-block; padding: 10px 20px; border-radius: 5px;'>%s</h3>"
                        + "<p style='margin-top: 30px;'>Insira este token na página de redefinição para atualizar sua senha.</p>"
                        + "</div>",
                user.getFullName(), user.getActivationToken()
        );

        sendEmail(user.getEmail(), subject, htmlContent);
    }

    // Método centralizado para envio de e-mails com Mailjet
    private void sendEmail(String recipientEmail, String subject, String htmlContent) {
        LOGGER.info("Enviando e-mail para '{}'", recipientEmail);

        try {
            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", senderEmail)
                                            .put("Name", senderName))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", recipientEmail)
                                                    .put("Name", "Usuário")))
                                    .put(Emailv31.Message.SUBJECT, subject)
                                    .put(Emailv31.Message.HTMLPART, htmlContent)));

            MailjetResponse response = mailjetClient.post(request);

            if (response.getStatus() == 200) {
                LOGGER.info("E-mail enviado com sucesso para '{}'.", recipientEmail);
            } else {
                LOGGER.error("Falha ao enviar e-mail para '{}': {}", recipientEmail, response.getData());
            }
        } catch (Exception e) {
            LOGGER.error("Erro ao enviar e-mail para '{}': {}", recipientEmail, e.getMessage());
        }
    }

    // Confirmar redefinição de senha com token
    public boolean confirmResetPassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByActivationToken(token);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setActivationToken(null);
            userRepository.save(user);
            LOGGER.info("Senha redefinida com sucesso para o usuário '{}'.", user.getUsername());
            return true;
        }

        LOGGER.warn("Token de redefinição inválido: {}", token);
        return false;
    }

    // Métodos de permissionamento
    public void addPermissionsToUser(Long userId, List<Long> permissionIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    
        List<Permission> permissions = permissionService.findByIds(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            throw new RuntimeException("Uma ou mais permissões não foram encontradas.");
        }
    
        for (Permission permission : permissions) {
            boolean alreadyExists = user.getUserPermissions().stream()
                    .anyMatch(up -> up.getPermission().equals(permission));
    
            if (alreadyExists) {
                LOGGER.warn("Permissão '{}' já existe para o usuário '{}'.", permission.getName(), user.getUsername());
                continue;
            }
    
            UserPermissionId userPermissionId = new UserPermissionId(user.getId(), permission.getId());
    
            UserPermission userPermission = new UserPermission();
            userPermission.setId(userPermissionId);
            userPermission.setUser(user);
            userPermission.setPermission(permission);
            userPermission.setGrantedAt(LocalDateTime.now());
    
            user.getUserPermissions().add(userPermission);
        }
    
        userRepository.save(user);
        LOGGER.info("Permissões '{}' adicionadas ao usuário '{}'.", permissionIds, user.getUsername());
    }    

    /**
     * Remove múltiplas permissões de um usuário.
     *
     * @param userId        ID do usuário.
     * @param permissionIds Lista de IDs das permissões.
     */
    public void removePermissionsFromUser(Long userId, List<Long> permissionIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Remove todas as permissões da lista
        boolean removed = user.getUserPermissions().removeIf(up -> 
                permissionIds.contains(up.getId().getPermissionId()));

        if (!removed) {
            throw new RuntimeException("Nenhuma permissão válida encontrada para remoção.");
        }

        // Salva o usuário após a remoção das permissões
        userRepository.save(user);
        LOGGER.info("Permissões '{}' removidas do usuário '{}'.", permissionIds, user.getUsername());
    }

    /**
     * Remove todas as permissões de um usuário.
     *
     * @param userId ID do usuário.
     */
    public void removeAllPermissionsFromUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Remove todas as permissões do conjunto
        user.getUserPermissions().clear();

        // Salva o usuário após a remoção
        userRepository.save(user);
        LOGGER.info("Todas as permissões foram removidas do usuário '{}'.", user.getUsername());
    }

    public void addUserToGroups(Long userId, List<Long> groupIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    
        List<Group> groups = groupService.findAllByIds(groupIds);
        if (groups.size() != groupIds.size()) {
            throw new RuntimeException("Um ou mais grupos não foram encontrados.");
        }
    
        if (user.getUserGroups() == null) {
            user.setUserGroups(new HashSet<>()); // Inicializa se estiver null
        }
    
        for (Group group : groups) {
            // Verifica se já existe uma associação
            boolean isAlreadyAssociated = user.getUserGroups().stream()
                    .anyMatch(userGroup -> userGroup.getGroup().equals(group));
    
            if (isAlreadyAssociated) {
                LOGGER.warn("Usuário '{}' já está associado ao grupo '{}'.", user.getUsername(), group.getName());
                continue;
            }
    
            // Cria o ID composto
            UserGroupId userGroupId = new UserGroupId();
            userGroupId.setUserId(user.getId());
            userGroupId.setGroupId(group.getId());
    
            // Cria a nova associação
            UserGroup userGroup = new UserGroup();
            userGroup.setId(userGroupId);
            userGroup.setUser(user);
            userGroup.setGroup(group);
            userGroup.setJoinedAt(LocalDateTime.now());
    
            // Adiciona ao conjunto de associações do usuário
            user.addUserGroup(userGroup);
        }
    
        userRepository.save(user);
        LOGGER.info("Usuário '{}' adicionado aos grupos '{}'.", user.getUsername(), groupIds);
    }    

    /**
     * Remove um usuário de múltiplos grupos.
     *
     * @param userId   ID do usuário.
     * @param groupIds Lista de IDs dos grupos.
     */
    public void removeUserFromGroups(Long userId, List<Long> groupIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        List<Group> groups = groupService.findAllByIds(groupIds);

        List<UserGroup> userGroupsToRemove = user.getUserGroups().stream()
                .filter(userGroup -> groups.contains(userGroup.getGroup()))
                .collect(Collectors.toList());

        if (userGroupsToRemove.isEmpty()) {
            throw new RuntimeException("Usuário não está associado a esses grupos.");
        }

        userGroupsToRemove.forEach(user::removeUserGroup); // Remove os grupos associados

        userRepository.save(user);
        LOGGER.info("Usuário '{}' foi removido dos grupos '{}'.", user.getUsername(), groupIds);
    }

    /**
     * Remove um usuário de todos os grupos.
     *
     * @param userId ID do usuário.
     */
    public void removeUserFromAllGroups(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    
        user.clearUserGroups(); // Usa o método conveniente para limpar grupos
    
        userRepository.save(user);
        LOGGER.info("Usuário '{}' foi removido de todos os grupos.", user.getUsername());
    }

    public boolean hasPermission(Long userId, String permissionName) {
        User user = userRepository.findByIdWithPermissions(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    
        boolean hasUserPermission = user.getUserPermissions().stream()
                .map(up -> up.getPermission().getName())
                .anyMatch(permissionName::equals);
    
        boolean hasGroupPermission = user.getUserGroups().stream()
                .flatMap(userGroup -> userGroup.getGroup().getGroupPermissions().stream())
                .map(gp -> gp.getPermission().getName())
                .anyMatch(permissionName::equals);
    
        return hasUserPermission || hasGroupPermission;
    }    

    private UserResponseDTO toUserResponseDTO(User user) {
        UserResponseDTO userResponse = UserResponseDTO.fromUser(user); // Converte os dados básicos
        if (userResponse != null) {
            // Adiciona as permissões como uma string (se necessário no contexto)
            Set<String> roleNames = user.getUserPermissions().stream()
                    .map(up -> up.getPermission().getName())
                    .collect(Collectors.toSet());
            userResponse.setRoles(roleNames); // Supondo que o UserResponseDTO tenha o campo roles.
        }
        return userResponse;
    }

    public void incrementLoginAttempts(String username) {
        User user = findByUsername(username);
        if (user != null) {
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            userRepository.save(user); // Atualiza no banco
        }
    }
    
    public void resetLoginAttempts(String username) {
        User user = findByUsername(username);
        if (user != null) {
            user.setLoginAttempts(0);
            userRepository.save(user); // Atualiza no banco
        }
    }
    
    public void blockUser(String username) {
        User user = findByUsername(username);
        if (user != null) {
            user.setStatus("bloqueado");
            user.setLoginAttempts(3); // Mantém o contador no máximo
            userRepository.save(user); // Atualiza no banco
        }
    }

    public boolean validateToken(String token) {
        try {
            // Implementação para verificar o JWT (validação de assinatura, expiração, etc.)
            return jwtUtil.isTokenValid(token); // jwtUtil pode ser uma classe utilitária para JWT
        } catch (Exception e) {
            LOGGER.error("Erro ao validar token: ", e);
            return false;
        }
    }

    public UserDetailsDTO getUserDetails(Long userId) {
        // Busca o usuário
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // Busca permissões diretas do usuário e converte para PermissionDTO
        List<PermissionDTO> permissions = userPermissionRepository.findEffectivePermissionsByUserId(userId).stream()
                .map(PermissionDTO::new) // Converte Permission para PermissionDTO
                .toList();

        // Busca grupos do usuário e converte para GroupDTO
        List<GroupDTO> groups = userGroupRepository.findGroupsWithPermissionsByUserId(userId).stream()
                .map(GroupDTO::new) // Converte Group para GroupDTO
                .toList();

        // Retorna os dados agregados no UserDetailsDTO
        return new UserDetailsDTO(user, groups, permissions);
    }
    
    /**
     * Retorna a lista de usuários como DTO, ocultando informações sensíveis.
     */
    public List<UserManagementDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retorna um usuário específico pelo ID como DTO.
     */
    public Optional<UserManagementDTO> getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toDTO);
    }

    /**
     * Atualiza o status do usuário (ativo, inativo, bloqueado).
     */
    @Transactional
    public GlobalResponse<String> updateUserStatus(Long userId, String status) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return GlobalResponse.error("Usuário não encontrado", 404);
        }

        User user = optionalUser.get();
        if (!status.equals("ativo") && !status.equals("inativo") && !status.equals("bloqueado")) {
            return GlobalResponse.error("Status inválido", 400);
        }

        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return GlobalResponse.success("Status atualizado com sucesso", null);
    }

    /**
     * Reseta o número de tentativas de login do usuário.
     */
    @Transactional
    public GlobalResponse<String> resetLoginAttempts(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return GlobalResponse.error("Usuário não encontrado", 404);
        }

        User user = optionalUser.get();
        user.setLoginAttempts(0);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return GlobalResponse.success("Tentativas de login resetadas com sucesso", null);
    }

    /**
     * Registra a data de último acesso do usuário.
     */
    @Transactional
    public GlobalResponse<String> updateLastAccess(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return GlobalResponse.error("Usuário não encontrado", 404);
        }

        User user = optionalUser.get();
        user.setLastAccess(LocalDateTime.now());
        userRepository.save(user);

        return GlobalResponse.success("Último acesso atualizado com sucesso", null);
    }
    
}    
