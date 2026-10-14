CREATE TABLE patients (
    id UUID PRIMARY KEY,
    medical_record_number VARCHAR(30) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_patients_mrn
        UNIQUE (medical_record_number),

    CONSTRAINT chk_patients_gender
        CHECK (gender IN ('MALE', 'FEMALE', 'OTHER', 'UNKNOWN'))
);