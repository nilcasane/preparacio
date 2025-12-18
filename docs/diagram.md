````mermaid
classDiagram
    direction TB

    %% ================================================================
    %% Persons and Roles
    %% ================================================================
    class Person {
        + id: Long
        + username: String<<unique>>
        + name: String
        + lastName: String 
        + phoneNumber: Integer
        + email: String<<unique>>
        + password: String
        + address: String
    }
    
    class Administrator {
        + roles: Set~Role~
    }
    
    class Role {
        + id: Long
        + name: ERole
    }
    
    class ERole {
        <<enumeration>>
        RECEPTIONIST
        CLINIC_MANAGER
    }
    
    class Veterinarian {
        + licenseNumber: Integer
        + yearsOfExperience: Integer
        + availability: List~Availability~
    }

    class PetOwner {
        + pets: List~Pet~
        + visits: List~Visit~
    }

    %% ================================================================
    %% Core Entities
    %% ================================================================
    class Pet {
        + id: Long
        + name: String
        + dateOfBirth: Date
        + gender: String
        + breed: String
        + weight: Double
        + microchipNumber: Long
        + petOwners: List~PetOwner~
        + visits: List~Visit~
    }
    
    %% ================================================================
    %% Availability Management
    %% ================================================================
    class Availability {
        + id: Long
        + dayOfWeek: Integer
        + startTime: Time
        + endTime: Time
        + periodStart: Date
        + periodEnd: Date
        + veterinarian: Veterinarian
        + exceptions: List~AvailabilityException~
    }
    
    class AvailabilityException {
        + id: Long
        + reason: String
        + dayOfWeek: Integer
        + startTime: Time
        + endTime: Time
        + periodStart: Date
        + periodEnd: Date
        + availability: Availability
    }
    
    %% ================================================================
    %% Visits and Treatments
    %% ================================================================
    class Visit {
        + id: Long
        + visitDate: Date
        + visitTime: Time
        + duration: Integer
        + reasonForVisit: String
        + diagnosis: String
        + notes: String
        + pricerPerFifteen: Double
        + status: VisitStatus
        + veterinarian: Veterinarian
        + pet: Pet
        + petOwner: PetOwner
        + treatment: Treatment
    }

    class VisitStatus {
        <<enumeration>>
        SCHEDULED
        COMPLETED
        CANCELLED
        IN_PROGRESS
    }

    class Treatment {
        + id: Long
        + name: String
        + description: String
        + cost: Double
    }
    
    %% ================================================================
    %% Medications
    %% ================================================================
    class Medication {
        + id: Long
        + name: String
        + activeIngredient: String
        + dosageUnit: Integer
        + unitPrice: Double
        + reorderThreshold: Integer
        + medicationBatches: List~MedicationBatch~
        + medicationIncompatibilities: List~MedicationIncompatibility~
    }
    
    class MedicationIncompatibility {
        + medication1: Medication
        + medication2: Medication
        + description: String
    }
    
    class MedicationBatch {
        + id: Long
        + lotNumber: Long
        + receivedDate: Date
        + expiryDate: Date
        + initialQuantity: Integer
        + currentQuantity: Integer
        + purchasePricePerUnit: Double
        + medication: Medication
    }

    class MedicationPrescription {
        + id: Long
        + dosageInstructions: String
        + quantityPrescribed: Integer
        + durationInDays: Integer
        + medication: Medication
        + visit: Visit
    }

    class LowStockAlert {
        + id: Long
        + medicationId: Long
        + alertDate: Date
        + acknowledged: Boolean       
    }

    %% ================================================================
    %% Invoicing and Promotions
    %% ================================================================
    class Invoice {
        + id: Long
        + invoiceDate: Date
        + totalAmount: Double
        + status: String
        + petOwner: PetOwner
        + visit: Visit
        + items: List~InvoiceItem~
        + payment: Payment
    }
    
    class InvoiceItem {
        + id: Long
        + invoice: Invoice
        + description: String
        + quantity: Integer
        + unitPrice: Double
        + totalPrice: Double
        + medicationPrescription: MedicationPrescription
        + treatment: Treatment
    }
    
    class InvoiceStatus {
        <<enumeration>>
        UNPAID
        PAID
    }
    
    class Payment {
        + id: Long
        + paymentDate: Date
        + amount: Double
        + paymentMethod: PaymentMethod
        + transactionReference: String
        + invoice: Invoice
    }
    
    class PaymentMethod {
        <<enumeration>>
        CASH
        CREDIT_CARD
        DEBIT_CARD
        BANK_TRANSFER
    }
    
    %% ================================================================
    %% Promotions
    %% ================================================================
    class Promotion {
        + id: Long
        + name: String
        + description: String
        + discountCode: String
        + startDate: Date
        + endDate: Date
        + discounts: List~Discount~
    }
    
    class Discount {
        + id: Long
        + code: String
        + discountType: DiscountType
        + valueAmount: Double
        + startDate: Date
        + endDate: Date
        + maxUses: Integer
        + usesCount: Integer
        + promotion: Promotion
        + loyaltyTiers: Set~LoyaltyTier~
    }
    
    class LoyaltyTier {
        + id: Long
        + name: String
        + description: String
        + minPoints: Integer
        + benefits: String
        + discount: Discount
    }

    class DiscountType {
        <<enumeration>>
        PERCENTAGE
        FIXED_AMOUNT
        LOYALTY_TIER
    }

    %% ================================================================
    %% Relationships
    %% ================================================================
    
    %% Inheritance relationships
    Person <|-- Administrator
    Administrator "1" --> "0..*" Role : has
    Role --> ERole : of type
    Person <|-- Veterinarian
    Person <|-- PetOwner
    
    %% Main relationships
    PetOwner "1..*" --> "0..*" Pet : owns
    Pet "1" --> "0..*" Visit : has
    PetOwner "1" --> "0..*" Visit : schedules

    %% Availability relationships
    Veterinarian "1" --> "0..*" Availability: has
    Availability "1" --> "0..*" AvailabilityException: contains
    
    %% Visit and Treatment relationships
    Visit --> VisitStatus : has
    Visit "1" --> "0..*" MedicationPrescription: generates
    Visit "1" --> "0..1" Treatment: includes
    Veterinarian "1" --> "0..*" Visit: conducts

    %% Medication relationships
    Medication "1" --> "0..*" MedicationBatch: consists of
    Medication "1" --> "0..1" LowStockAlert: triggers
    Medication "1" --> "0..*" MedicationIncompatibility: has
    Medication "1" --> "0..*" MedicationPrescription: prescribed in
    
    %% Invoicing relationships
    PetOwner "1" --> "0..*" Invoice: receives
    Visit "1" --> "0..1" Invoice: billed in
    Invoice "1" --> "0..*" InvoiceItem: includes
    InvoiceItem "0..1" --> "0..1" MedicationPrescription: for
    Treatment "1" <-- "0..*" InvoiceItem: for
    
    %% Payment relationships
    Invoice "1" <-- "1" Payment : paid by
    Invoice --> InvoiceStatus : has
    Payment --> PaymentMethod : using
    
    %% Promotions relationships
    Promotion "1" --> "0..*" Discount: offers
    Discount "0..1" <-- "0..*" LoyaltyTier: applies to
    Discount --> DiscountType : has
````