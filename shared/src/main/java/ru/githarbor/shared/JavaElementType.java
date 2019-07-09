package ru.githarbor.shared;

public enum JavaElementType {
    CLASS(0),
    FIELD(1),
    PARAMETER(2),
    LOCAL_VARIABLE(3),
    TYPE_EXPRESSION(4),
    LAMBDA_STATEMENT(5),
    METHOD(6),
    CONSTRUCTOR(7),
    METHOD_CALL_EXPRESSION(8),
    REFERENCE_EXPRESSION(9),
    DECLARATION_STATEMENT(10),
    REFERENCE_STATEMENT(11),
    NAME(12),
    ASSIGN_EXPRESSION(13),
    THIS_EXPRESSION(14),
    NAME_EXPRESSION(15),
    FIELD_ACCESS_EXPRESSION(16),
    FOR_STATEMENT(17),
    FOR_EACH_STATEMENT(18),
    TRY_STATEMENT(19),
    TRY_BLOCK_STATEMENT(20),
    CATCH_CLAUSE_STATEMENT(21),
    FINALLY_BLOCK_STATEMENT(22),
    SWITCH_STATEMENT(23),
    SWITCH_ENTRY_STATEMENT(24),
    INITIALIZER_STATEMENT(25),
    OBJECT_CREATION_EXPRESSION(26),
    ENUM(27),
    ANONYMOUS_CLASS_BODY_STATEMENT(28);

    public final int type;

    JavaElementType(int type) {
        this.type = type;
    }

    public static JavaElementType valueOf(int key) {
        return values()[key];
    }
}
