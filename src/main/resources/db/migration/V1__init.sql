DROP TABLE IF EXISTS card;

CREATE TABLE IF NOT EXISTS card
(
    id                  UUID primary key default gen_random_uuid(),
    name                varchar(100) not null,
    manaCost            varchar(100) not null,
    cmc                 integer not null,
    color               text[],
    colorIdentity       text[],
    type                varchar(100) not null,
    types               text[],
    subtypes            text[],
    rarity              varchar(100) not null,
    set                 varchar(100) not null,
    setName             varchar(100) not null,
    text                varchar(1000) not null,
    flavor              varchar(100),
    artist              varchar(100) not null,
    number              integer not null,
    power               integer not null,
    toughness           integer not null,
    imageUrl            varchar(100) not null,
    multiverseId        varchar(100),
    legalities          varchar(100),
    race                varchar(100) not null
);

INSERT INTO card(name, manacost, cmc, color, coloridentity, type, types, subtypes, rarity, set, setname, text, flavor, artist, number, power, toughness, imageurl, multiverseid, legalities, race)
VALUES ('Inquisitrice Greyfax', '{1}{L}{W}{B}', 4, ARRAY ['L', 'w', 'B'], ARRAY ['L', 'w', 'B'], 'Créature légendaire - Humain et Inquisiteur', ARRAY ['Creature'], NULL, 'Mythic', '40k', 'Warhammer 40,000 Commander', 'Vigilance\nSagesse incontestable - Les autres créatures que vous contrôlez gagnent +1/+0 et ont la vigilance.\nChasse a l hérésie - Engagez une créature ciblée qu un adversaire contrôle.\nEnqêtez. (Créer un jeton indice. C est un artefact avec 2 mana sacrifiez cet artefact : Piochez une carte.',
        NULL, 'Lie Setiawan', 1, 3, 3, 'Pas encore fait', NULL, NULL, 'Imperium');
