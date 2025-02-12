package tim.field.application.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.util.Set;

public class CustomPhysicalNamingStrategy implements PhysicalNamingStrategy {

    // 🔹 Lista das colunas que estão em camelCase na tabela e NÃO devem ser convertidas
    private static final Set<String> EXCLUDED_COLUMNS = Set.of(
            "activityId", "apptNumber", "activityType", "resourceInternalId",
            "resourceId", "stateProvince", "startTime", "endTime",
            "workZone", "activityGroup", "collectDate"
    );

    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment context) {
        return name;
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
        return name;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        // Converter todas as tabelas para snake_case
        return Identifier.toIdentifier(name.getText().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase());
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
        return name;
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        // 🔹 Se a coluna estiver na lista de exclusões, manter o nome original
        if (EXCLUDED_COLUMNS.contains(name.getText())) {
            return name;
        }

        // 🔹 Converter todas as outras colunas para snake_case
        return Identifier.toIdentifier(name.getText().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase());
    }
}