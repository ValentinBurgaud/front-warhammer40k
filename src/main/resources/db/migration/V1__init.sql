DROP TABLE IF EXISTS card;

CREATE TABLE IF NOT EXISTS card
(
    id                  UUID primary key default gen_random_uuid(),
    name                varchar(100) not null,
    mana_cost           varchar(100) not null,
    cmc                 integer not null,
    color               text[],
    color_identity      text[],
    type                varchar(100) not null,
    types               text[],
    subtypes            text[],
    rarity              varchar(100) not null,
    set                 varchar(100) not null,
    set_name            varchar(100) not null,
    text                varchar(1000) not null,
    flavor              varchar(100),
    artist              varchar(100) not null,
    number              integer not null,
    power               integer not null,
    toughness           integer not null,
    multiverse_id       varchar(100),
    legalities          varchar(100),
    race                varchar(100) not null
);

CREATE TABLE IF NOT EXISTS image
(
    id                  UUID primary key default gen_random_uuid(),
    image               bytea not null,
    image_name          varchar(256) not null,
    image_size          bigint not null,
    image_content_type  varchar(100) not null,
    card_id             UUID not null
);

INSERT INTO card(name, mana_cost, cmc, color, color_identity, type, types, subtypes, rarity, set, set_name, text, flavor, artist, number, power, toughness, multiverse_id, legalities, race)
VALUES ('Inquisitrice Greyfax', '{1}{L}{W}{B}', 4, ARRAY ['L', 'w', 'B'], ARRAY ['L', 'w', 'B'], 'Créature légendaire - Humain et Inquisiteur', ARRAY ['Creature'], NULL, 'Mythic', '40k', 'Warhammer 40,000 Commander', 'Vigilance\nSagesse incontestable - Les autres créatures que vous contrôlez gagnent +1/+0 et ont la vigilance.\nChasse a l hérésie - Engagez une créature ciblée qu un adversaire contrôle.\nEnqêtez. (Créer un jeton indice. C est un artefact avec 2 mana sacrifiez cet artefact : Piochez une carte.',
        NULL, 'Lie Setiawan', 1, 3, 3, NULL, NULL, 'Imperium');
