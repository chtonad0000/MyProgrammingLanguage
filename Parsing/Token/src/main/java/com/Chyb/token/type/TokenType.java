package com.Chyb.token.type;

public enum TokenType {
    IDENTIFIER,       // Имена переменных, функций и других идентификаторов.

    // Литералы
    NUMBER,           // Числовые литералы (целые или с плавающей точкой).
    STRING_LITERAL,   // Строковые литералы (например, "Hello").
    CHAR_LITERAL,     // Символьные литералы (например, 'a').
    BOOLEAN_LITERAL,  // Логические значения true или false.
    NULL_LITERAL,     // null, если поддерживается.

    // Операторы
    OPERATOR,         // +, -, *, /, =, ==, !=, &&, || и т.д.
    ASSIGNMENT,       // = для присваивания.
    INCREMENT,        // ++ для увеличения.
    DECREMENT,        // -- для уменьшения.

    // Пунктуация
    PUNCTUATION,      // Запятая, точка с запятой и другие символы.
    LPAREN,           // Левая круглая скобка (.
    RPAREN,           // Правая круглая скобка ).
    LBRACE,           // Левая фигурная скобка {.
    RBRACE,           // Правая фигурная скобка }.
    LBRACKET,         // Левая квадратная скобка [.
    RBRACKET,         // Правая квадратная скобка ].
    COMMA,            // Запятая ,.
    SEMICOLON,        // Точка с запятой ;.
    COLON,            // Двоеточие :.
    DOT,              // Точка ..

    // Специфические токены
    STRING,           // Для строк в общем.
    COMMENT,          // Комментарии (однострочные // и многострочные /* */).

    // Ключевые слова управления
    IF,               // if
    ELSE,             // else
    FOR,              // for
    WHILE,            // while
    RETURN,           // return
    BREAK,            // break
    CONTINUE,
    NEW,

    // Прочие ключевые слова
    FUNCTION,         // function
    VOID,             // void
    TYPE,             // Типы данных (например, int, float, boolean).

    // Ошибки и некорректные токены
    INVALID           // Некорректные символы или ошибки.
}
