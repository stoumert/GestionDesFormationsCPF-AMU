package amu.ter.cpf.model.entity;

public enum Role {
    ADMIN,
    MANAGER,
    USER;
    public static Role fromAffiliation(String affiliation) {
        return switch (affiliation) {
            case "faculty", "employee" ,"research" -> Role.MANAGER;
            default -> Role.USER;
        };
    }

}
