-- copypasted from database by hand
CREATE TABLE IF NOT EXISTS public.seuser
(
    userid bigint NOT NULL DEFAULT nextval('"User_UserID_seq"'::regclass),
    username text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "User_pkey" PRIMARY KEY (userid),
    CONSTRAINT "UQ_seuser_username" UNIQUE (username)
)

CREATE TABLE IF NOT EXISTS public.servertime
(
    "time" timestamp(0) without time zone NOT NULL,
    CONSTRAINT servertime_pkey PRIMARY KEY ("time")
)

CREATE TABLE IF NOT EXISTS public.authuser
(
    userid bigint NOT NULL,
    secret text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "PK_authuser" PRIMARY KEY (userid),
    CONSTRAINT "FK_seuser_authuser" FOREIGN KEY (userid)
        REFERENCES public.seuser (userid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE IF NOT EXISTS public.banks
(
    bankid bigint NOT NULL DEFAULT nextval('banks_bankid_seq'::regclass),
    bankname text COLLATE pg_catalog."default" NOT NULL,
    bankinitials text COLLATE pg_catalog."default" NOT NULL,
    loaninterest double precision NOT NULL DEFAULT 1.0,
    depositinterest double precision NOT NULL DEFAULT 1.0,
    vaultbalance numeric(102,2) NOT NULL DEFAULT 0,
    CONSTRAINT "PK_banks" PRIMARY KEY (bankid),
    CONSTRAINT "UQ_Bank_bankinitials" UNIQUE (bankinitials),
    CONSTRAINT "UQ_Bank_bankname" UNIQUE (bankname)
)

CREATE TABLE IF NOT EXISTS public.bankaccounts
(
    userid bigint NOT NULL,
    bankid bigint NOT NULL,
    balance numeric(102,2) NOT NULL,
    accountid bigint NOT NULL DEFAULT nextval('bankaccounts_accountid_seq'::regclass),
    accountnumber text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT bankaccounts_pkey PRIMARY KEY (accountid),
    CONSTRAINT "UQ_accountnumber" UNIQUE (accountnumber),
    CONSTRAINT "FK_bankaccounts_banks" FOREIGN KEY (bankid)
        REFERENCES public.banks (bankid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "FK_bankaccounts_seuser" FOREIGN KEY (userid)
        REFERENCES public.seuser (userid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE IF NOT EXISTS public.transactions
(
    transactionid bigint NOT NULL DEFAULT nextval('transactions_transactionid_seq'::regclass),
    fromid bigint NOT NULL,
    toid bigint NOT NULL,
    amount numeric(102,2) NOT NULL,
    transactiontime timestamp(2) with time zone NOT NULL,
    CONSTRAINT "PK_transactions" PRIMARY KEY (transactionid),
    CONSTRAINT "FK_bankaccounts_transaction_from" FOREIGN KEY (fromid)
        REFERENCES public.bankaccounts (accountid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT "FK_bankaccounts_transaction_to" FOREIGN KEY (toid)
        REFERENCES public.bankaccounts (accountid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
