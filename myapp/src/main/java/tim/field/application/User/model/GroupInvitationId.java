package tim.field.application.User.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GroupInvitationId implements Serializable {

    private Long groupId;
    private Long invitationCodeId;

    public GroupInvitationId() {}

    public GroupInvitationId(Long groupId, Long invitationCodeId) {
        this.groupId = groupId;
        this.invitationCodeId = invitationCodeId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getInvitationCodeId() {
        return invitationCodeId;
    }

    public void setInvitationCodeId(Long invitationCodeId) {
        this.invitationCodeId = invitationCodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupInvitationId that = (GroupInvitationId) o;
        return Objects.equals(groupId, that.groupId) &&
               Objects.equals(invitationCodeId, that.invitationCodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, invitationCodeId);
    }
}