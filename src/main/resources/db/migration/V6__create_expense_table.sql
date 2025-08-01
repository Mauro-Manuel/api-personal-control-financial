CREATE TABLE tb_expense (
    id SERIAL PRIMARY KEY,
    category_id INTEGER NOT NULL,
    detail_id INTEGER NOT NULL,
    expense_date DATE NOT NULL,
    description TEXT,
    month INTEGER,
    year INTEGER,
    created_at DATE NOT NULL,
    updated_at DATE,
    CONSTRAINT fk_expense_category
        FOREIGN KEY (category_id)
            REFERENCES expense_category (id)
            ON DELETE RESTRICT,
    CONSTRAINT fk_expense_detail
        FOREIGN KEY (detail_id)
            REFERENCES expense_detail (id)
            ON DELETE RESTRICT
);
