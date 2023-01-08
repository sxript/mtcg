drop table if exists Deck, Package, Trade, Card, Users, Stats, Profile, BattleLog;
drop type if exists Element;

-- CreateEnum
CREATE TYPE Element AS ENUM ('NORMAL', 'FIRE', 'WATER');

-- CreateTable
CREATE TABLE Users
(
    "id"       TEXT    NOT NULL,
    "name"     TEXT,
    "username" TEXT    NOT null UNIQUE,
    "password" TEXT    NOT NULL,
    "coins"    INTEGER NOT NULL,

    CONSTRAINT "User_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE Stats
(
    "id"     TEXT    NOT NULL,
    "elo"    INTEGER NOT NULL,
    "wins"   INTEGER NOT NULL,
    "losses" INTEGER NOT NULL,
    "draws"  INTEGER NOT NULL,
    "userid" TEXT    NOT null UNIQUE,

    CONSTRAINT "Stats_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE Profile
(
    "id"     TEXT NOT NULL,
    "bio"    TEXT,
    "image"  TEXT,
    "userid" TEXT NOT null UNIQUE,

    CONSTRAINT "Profile_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE Package
(
    "id"    TEXT    NOT NULL,
    "price" INTEGER NOT NULL,

    CONSTRAINT "Package_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE Card
(
    "id"          TEXT             NOT NULL,
    "name"        TEXT             NOT NULL,
    "damage"      DOUBLE PRECISION NOT NULL,
    "element"     Element          NOT NULL,
    "description" TEXT,
    "package_id"  TEXT,
    "user_id"     TEXT,
    "deck_id"     TEXT,

    CONSTRAINT "Card_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE Deck
(
    "id"      TEXT NOT NULL,
    "user_id" TEXT NOT null UNIQUE,

    CONSTRAINT "Deck_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE Trade
(
    "id"         TEXT NOT NULL,
    "card_id"    TEXT NOT null UNIQUE,
    "type"       TEXT,
    "min_damage" INTEGER,
    "coins"      INTEGER,

    CONSTRAINT "Trade_pkey" PRIMARY KEY ("id")
);

create table BattleLog
(
    "id"      TEXT NOT NULL,
    "log"     JSON,
    "message" text not null
);


-- AddForeignKey
ALTER TABLE Stats
    ADD CONSTRAINT "Stats_userid_fkey" FOREIGN KEY ("userid") REFERENCES Users ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE Profile
    ADD CONSTRAINT "Profile_userid_fkey" FOREIGN KEY ("userid") references Users ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE Card
    ADD CONSTRAINT "Card_package_id_fkey" FOREIGN KEY ("package_id") REFERENCES Package ("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE Card
    ADD CONSTRAINT "Card_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES Users ("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE Card
    ADD CONSTRAINT "Card_deck_id_fkey" FOREIGN KEY ("deck_id") REFERENCES Deck ("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE Deck
    ADD CONSTRAINT "Deck_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES Users ("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE Trade
    ADD CONSTRAINT "Trade_card_id_fkey" FOREIGN KEY ("card_id") REFERENCES Card ("id") ON DELETE RESTRICT ON UPDATE CASCADE;
