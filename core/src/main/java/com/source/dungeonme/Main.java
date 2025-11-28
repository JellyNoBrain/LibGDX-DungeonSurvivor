package com.source.dungeonme;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

public class Main extends ApplicationAdapter {
    SpriteBatch batch;
    OrthographicCamera camera;
    BitmapFont font;
    AssetManager assetManager;
    private Viewport viewport;

    enum State { MENU, PLAYING, GAME_OVER, PAUSED, VICTORY }
    State currentState = State.MENU;

    // --- ENUM VẬT PHẨM ---
    enum ItemType {
        HELMET_WINDS, ARMOR_WINDS, CRUSADER_SHIELD, PENDULUM, IRON_SWORD, BOW,
        HEALTH_POTION, CHOCOLATE_CAKE, APPLE,
        HELMET, ARMOR, BARE_SHIELD, GOLDEN_BOW
    }

    // --- CẤU HÌNH GAME ---
    final int TILE_SIZE = 64;
    final float PLAYER_SIZE = 40;
    final float DETECTION_RADIUS = 300f;

    // Map Data (3 Map nối dài)
    int[][] map1 = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, -1},
        {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    int[][] map2 = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 1},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    int[][] map3 = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    final int MAP1_WIDTH = map1[0].length;
    final int MAP2_WIDTH = map2[0].length;
    final int MAP3_WIDTH = map3[0].length;
    final int MAP_HEIGHT = map1.length;
    final int TOTAL_MAP_WIDTH = MAP1_WIDTH + MAP2_WIDTH + MAP3_WIDTH;
    int[][] mapData = new int[MAP_HEIGHT][TOTAL_MAP_WIDTH];

    int mapHeight = mapData.length;
    int mapWidth = mapData[0].length;

    // --- OBJECTS ---
    Rectangle player;
    Vector2 facingDir;
    float stateTime = 0f;
    float playerRotation = 0f;

    // Stats
    int playerHp = 5, maxHp = 5;
    int playerArmor = 0, maxArmor = 0;
    int baseDamage = 1;
    float playerHitTimer = 0, damageCooldown = 0;
    float attackTimer = 0; // Animation chém kiếm

    // --- EQUIPMENT FLAGS (ĐÃ KHAI BÁO LẠI ĐỂ SỬA LỖI) ---
    boolean hasShield = false;
    boolean hasPendulum = false;
    boolean isMelee = false; // False = Bow, True = Sword

    ItemType equipArmor = null;
    ItemType equipHelmet = null;
    ItemType equipShield = null;
    ItemType equipWeapon = ItemType.BOW;

    int shieldDurability = 5;

    Array<ItemType> inventory;
    Array<DroppedItem> droppedItems;

    Array<Bullet> bullets;
    Array<Goblin> enemies;

    // Map & Stage
    Array<Chest> lootChests = new Array<>();
    DemonBoss boss; // Object Boss

    enum StageState { FIRST_ROOM, CHEST_ROOM, BOSS_ROOM }
    StageState stageState = StageState.FIRST_ROOM;

    int chestRoomStartX = MAP1_WIDTH * TILE_SIZE;
    int bossRoomStartX  = (MAP1_WIDTH + MAP2_WIDTH) * TILE_SIZE;

    // Audio & UI
    Sound soundShoot, soundHit;
    Texture imgBlank, imgSpawnMark;
    java.util.Map<ItemType, Texture> itemTextures;

    Animation<TextureRegion> chestOpenAnim;
    Texture imgOpenedChest;

    // --- UI STAGE (DÙNG CHUNG CHO PAUSE & HUD) ---
    private Stage uiStage;
    private Skin skin;
    private ShapeRenderer shapeRenderer;
    private InputMultiplexer multiplexer;

    // --- SPAWN LOGIC ---
    float spawnDelayTimer = 0f;
    float spawnDelayDuration = 2.0f;
    boolean waveSpawned = false;
    Array<Vector2> pendingSpawnPoints = new Array<>();
    boolean areSpawnPointsGenerated = false;

    // --- COMBAT BUFFS ---
    float slowTimer = 0; // Bị làm chậm khi đỡ khiên

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camera);
        viewport.apply();
        font = new BitmapFont();

        assetManager = new AssetManager();
        // Load Assets
        assetManager.load("knight.png", Texture.class);
        assetManager.load("floor.png", Texture.class);
        assetManager.load("bricks.png", Texture.class);
        assetManager.load("goblin faceless.png", Texture.class);
        assetManager.load("eyes.png", Texture.class);
        assetManager.load("big eyes.png", Texture.class);
        assetManager.load("evil eyes.png", Texture.class);
        assetManager.load("iron sword.png", Texture.class);
        assetManager.load("bow.png", Texture.class);
        assetManager.load("door.png", Texture.class);
        assetManager.load("demon.png", Texture.class);

        assetManager.load("chest.png", Texture.class);
        assetManager.load("chest opening animation.png", Texture.class);
        assetManager.load("opened chest.png", Texture.class);

        assetManager.load("helmet of the winds.png", Texture.class);
        assetManager.load("armor of the winds.png", Texture.class);
        assetManager.load("crusader's shield.png", Texture.class);
        assetManager.load("pendulum.png", Texture.class);
        assetManager.load("health potion.png", Texture.class);
        assetManager.load("chocolate cake.png", Texture.class);
        assetManager.load("apple.png", Texture.class);

        try { assetManager.load("helmet.png", Texture.class); } catch(Exception e){}
        try { assetManager.load("armor.png", Texture.class); } catch(Exception e){}
        try { assetManager.load("bare shield.png", Texture.class); } catch(Exception e){}

        try {
            assetManager.load("shoot.wav", Sound.class);
            assetManager.load("hit.wav", Sound.class);
        } catch (Exception e) { Gdx.app.log("Warn", "Missing sound files"); }

        assetManager.finishLoading();

        if (assetManager.isLoaded("shoot.wav")) soundShoot = assetManager.get("shoot.wav", Sound.class);
        if (assetManager.isLoaded("hit.wav")) soundHit = assetManager.get("hit.wav", Sound.class);

        // Chest Animation
        Texture sheetChest = assetManager.get("chest opening animation.png", Texture.class);
        int frameCols = 6, frameRows = 1;
        TextureRegion[][] tmp = TextureRegion.split(sheetChest, sheetChest.getWidth() / frameCols, sheetChest.getHeight() / frameRows);
        chestOpenAnim = new Animation<>(0.1f, tmp[0]);
        chestOpenAnim.setPlayMode(Animation.PlayMode.NORMAL);
        imgOpenedChest = assetManager.get("opened chest.png", Texture.class);

        // Init Textures Map
        itemTextures = new java.util.HashMap<>();
        itemTextures.put(ItemType.HELMET_WINDS, assetManager.get("helmet of the winds.png", Texture.class));
        itemTextures.put(ItemType.ARMOR_WINDS, assetManager.get("armor of the winds.png", Texture.class));
        itemTextures.put(ItemType.CRUSADER_SHIELD, assetManager.get("crusader's shield.png", Texture.class));
        itemTextures.put(ItemType.PENDULUM, assetManager.get("pendulum.png", Texture.class));
        itemTextures.put(ItemType.IRON_SWORD, assetManager.get("iron sword.png", Texture.class));
        itemTextures.put(ItemType.BOW, assetManager.get("bow.png", Texture.class));
        itemTextures.put(ItemType.HEALTH_POTION, assetManager.get("health potion.png", Texture.class));
        itemTextures.put(ItemType.CHOCOLATE_CAKE, assetManager.get("chocolate cake.png", Texture.class));
        itemTextures.put(ItemType.APPLE, assetManager.get("apple.png", Texture.class));

        if(assetManager.isLoaded("helmet.png")) itemTextures.put(ItemType.HELMET, assetManager.get("helmet.png", Texture.class));
        if(assetManager.isLoaded("armor.png")) itemTextures.put(ItemType.ARMOR, assetManager.get("armor.png", Texture.class));
        if(assetManager.isLoaded("bare shield.png")) itemTextures.put(ItemType.BARE_SHIELD, assetManager.get("bare shield.png", Texture.class));

        // Textures
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE); pixmap.fill();
        imgBlank = new Texture(pixmap);

        com.badlogic.gdx.graphics.Pixmap pMark = new com.badlogic.gdx.graphics.Pixmap(64, 64, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pMark.setColor(new Color(1, 0, 0, 0.5f)); pMark.fillCircle(32, 32, 30);
        imgSpawnMark = new Texture(pMark);

        pixmap.dispose(); pMark.dispose();

        // UI & Input
        shapeRenderer = new ShapeRenderer();
        uiStage = new Stage(new ScreenViewport()); // [SỬA LỖI] Dùng uiStage thay vì pauseStage
        try { skin = new Skin(Gdx.files.internal("uiskin.json")); } catch (Exception e) {}

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    if (currentState == State.PLAYING) pause();
                    else if (currentState == State.PAUSED) resume();
                    return true;
                }
                if (keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_5) {
                    useItem(keycode - Input.Keys.NUM_1);
                    return true;
                }
                return false;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);

        copyMapsIntoMapData();
        resetGame();
    }

    private void copyMapsIntoMapData() {
        for (int row = 0; row < MAP_HEIGHT; row++) {
            for (int col = 0; col < MAP1_WIDTH; col++) {
                mapData[row][col] = map1[row][col];
            }
            for (int col = 0; col < MAP2_WIDTH; col++) {
                mapData[row][MAP1_WIDTH + col] = map2[row][col];
            }
            for (int col = 0; col < MAP3_WIDTH; col++) {
                mapData[row][MAP1_WIDTH + MAP2_WIDTH + col] = map3[row][col];
            }
        }
    }

    private void resetGame() {
        player = new Rectangle(150, 150, PLAYER_SIZE, PLAYER_SIZE);
        facingDir = new Vector2(1, 0);
        playerHp = 5;
        maxHp = 5;
        playerArmor = 0;
        maxArmor = 0;
        baseDamage = 1;

        equipArmor = null;
        equipHelmet = null;
        equipShield = null;
        equipWeapon = ItemType.BOW;
        shieldDurability = 5;

        // Reset flags
        hasShield = false;
        hasPendulum = false;
        isMelee = false;

        bullets = new Array<>();
        enemies = new Array<>();
        lootChests = new Array<>();
        droppedItems = new Array<>();
        inventory = new Array<>();

        spawnDelayTimer = 0f;
        waveSpawned = false;
        areSpawnPointsGenerated = false;
        pendingSpawnPoints.clear();

        stageState = StageState.FIRST_ROOM;
        boss = null;
    }

    private void generateSpawnPoints(int count) {
        pendingSpawnPoints.clear();
        for (int i = 0; i < count; i++) {
            float x, y;
            int safetyLoop = 0;
            do {
                x = MathUtils.random(TILE_SIZE, (MAP1_WIDTH - 1) * TILE_SIZE);
                y = MathUtils.random(TILE_SIZE, (MAP_HEIGHT - 1) * TILE_SIZE);
                safetyLoop++;
            } while (isWall(x, y, 40) && safetyLoop < 100);
            pendingSpawnPoints.add(new Vector2(x, y));
        }
        areSpawnPointsGenerated = true;
    }

    private void spawnEnemyAt(float x, float y) {
        String[] eyes = {"eyes.png", "big eyes.png", "evil eyes.png"};
        Texture randomEye = assetManager.get(eyes[MathUtils.random(0, 2)], Texture.class);
        enemies.add(new Goblin(x, y, 40, randomEye));
    }

    private void spawnBoss() {
        boss = new DemonBoss(bossRoomStartX + 460, 260 , 100);
    }

    private void spawnChest(int id) {
        float x, y;
        int safetyLoop = 0;
        Rectangle newChest;
        do {
            x = MathUtils.random(chestRoomStartX + TILE_SIZE, bossRoomStartX - TILE_SIZE);
            y = MathUtils.random(TILE_SIZE, MAP_HEIGHT * TILE_SIZE - TILE_SIZE);
            newChest = new Rectangle(x, y, 40, 40);
            safetyLoop++;
        } while ((isWall(x, y, 40) || overlapsChest(newChest)) && safetyLoop < 100);

        Chest c = new Chest(x, y);
        c.id = id;
        lootChests.add(c);
    }

    private boolean overlapsChest(Rectangle chest) {
        for (Chest c : lootChests) { if (c.rect.overlaps(chest)) return true; }
        return false;
    }

    private void useItem(int index) {
        if (index >= 0 && index < inventory.size) {
            ItemType item = inventory.get(index);
            boolean used = false;

            if (item == ItemType.HEALTH_POTION) {
                if(playerHp < maxHp) { playerHp = Math.min(playerHp + 3, maxHp); used = true; }
            } else if (item == ItemType.CHOCOLATE_CAKE) {
                if(playerHp < maxHp) { playerHp = Math.min(playerHp + 2, maxHp); used = true; }
            } else if (item == ItemType.APPLE) {
                if(playerHp < maxHp) { playerHp = Math.min(playerHp + 1, maxHp); used = true; }
            }

            if (used) inventory.removeIndex(index);
        }
    }

    // --- CHEST & LOOT LOGIC ---
    private void triggerChestOpen(Chest chest) {
        chest.isOpening = true;
        float dropX = chest.rect.x;
        float dropY = chest.rect.y - 40;

        switch (chest.id) {
            case 0: dropItem(ItemType.HELMET_WINDS, dropX, dropY); break;
            case 1: dropItem(ItemType.ARMOR_WINDS, dropX, dropY); break;
            case 2: dropItem(ItemType.IRON_SWORD, dropX, dropY); break;
            case 3: dropItem(ItemType.CRUSADER_SHIELD, dropX, dropY); break;
            case 4:
                dropItem(ItemType.HEALTH_POTION, dropX - 20, dropY);
                dropItem(ItemType.CHOCOLATE_CAKE, dropX, dropY + 20);
                dropItem(ItemType.APPLE, dropX + 20, dropY);
                break;
            case 5: dropItem(ItemType.PENDULUM, dropX, dropY); break;
        }
    }

    private void dropItem(ItemType type, float x, float y) {
        droppedItems.add(new DroppedItem(x, y, type));
    }

    private void pickUpItem(DroppedItem item) {
        droppedItems.removeValue(item, true);
        switch (item.type) {
            case IRON_SWORD: equipWeapon = ItemType.IRON_SWORD; isMelee = true; break;
            case BOW: equipWeapon = ItemType.BOW; isMelee = false; break;
            case PENDULUM: hasPendulum = true; baseDamage = 2; break;
            case CRUSADER_SHIELD: hasShield = true; equipShield = item.type; shieldDurability = 5; break;
            case HELMET_WINDS: equipHelmet = item.type; maxArmor += 2; playerArmor += 2; break;
            case ARMOR_WINDS: equipArmor = item.type; maxArmor += 2; playerArmor += 2; break;
            default: if (inventory.size < 5) inventory.add(item.type); break;
        }
    }

    @Override
    public void render() {
        viewport.apply();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        switch (currentState) {
            case MENU:
                updateMenu();
                break;
            case PLAYING:
                updateGameLogic(deltaTime);
                drawGame(deltaTime);
                if (playerHp <= 0) {
                    currentState = State.GAME_OVER;
                    camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);
                    camera.update();
                }
                if (stageState == StageState.BOSS_ROOM && boss != null && boss.hp <= 0) {
                    currentState = State.VICTORY; // [SỬA LỖI] Đã thêm trạng thái VICTORY
                    createVictoryMenu();
                    Gdx.input.setInputProcessor(multiplexer);
                }
                break;
            case GAME_OVER:
                updateGameOver();
                break;
            case VICTORY:
                drawGame(0);
                drawPauseOverlay();
                uiStage.getViewport().apply();
                uiStage.act(deltaTime);
                uiStage.draw();
                break;
            case PAUSED:
                drawGame(0);
                drawPauseOverlay();
                uiStage.getViewport().apply();
                uiStage.act(deltaTime);
                uiStage.draw();
                break;
        }
    }

    private void updateMenu() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            resetGame();
            currentState = State.PLAYING;
        }
        batch.begin();
        font.getData().setScale(2);
        font.setColor(Color.YELLOW);
        font.draw(batch, "DUNGEON SURVIVOR", 250, 300);
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        if ((System.currentTimeMillis() / 500) % 2 == 0) {
            font.draw(batch, "Press ENTER to Start", 280, 200);
        }
        batch.end();
    }

    private void updateGameOver() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            resetGame();
            currentState = State.PLAYING;
        }
        batch.begin();
        font.getData().setScale(2);
        font.setColor(Color.RED);
        font.draw(batch, "GAME OVER", 320, 300);
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Press ENTER to Restart", 280, 200);
        batch.end();
    }

    private void updateGameLogic(float deltaTime) {
        stateTime += deltaTime;
        if (slowTimer > 0) slowTimer -= deltaTime;

        // --- SPAWN LOGIC ---
        if (stageState == StageState.FIRST_ROOM && !waveSpawned) {
            if (!areSpawnPointsGenerated) generateSpawnPoints(5);
            spawnDelayTimer += deltaTime;
            if (spawnDelayTimer >= spawnDelayDuration) {
                for(Vector2 point : pendingSpawnPoints) spawnEnemyAt(point.x, point.y);
                pendingSpawnPoints.clear();
                waveSpawned = true;
            }
        }

        // Logic Chuyển Stage
        if(stageState == StageState.FIRST_ROOM && waveSpawned && enemies.isEmpty()) {
            stageState = StageState.CHEST_ROOM;
        } else if(stageState == StageState.CHEST_ROOM) {
            if(lootChests.isEmpty()) {
                for(int i=0; i<6; i++) spawnChest(i);
            }

            for(Chest c : lootChests) {
                if(!c.isOpen && !c.isOpening && player.overlaps(c.rect)) {
                    triggerChestOpen(c);
                }
            }

            Iterator<DroppedItem> it = droppedItems.iterator();
            while(it.hasNext()){
                DroppedItem item = it.next();
                if(player.overlaps(item.rect)) {
                    pickUpItem(item);
                }
            }

            boolean allOpened = true;
            for(Chest c : lootChests) if(!c.isOpen) allOpened = false;
            if(allOpened && droppedItems.isEmpty()) stageState = StageState.BOSS_ROOM;
        } else if(stageState == StageState.BOSS_ROOM) {
            if (boss == null) spawnBoss();
            else {
                boss.update(deltaTime, player);
                Rectangle bossHitbox = new Rectangle(boss.x+20, boss.y+20, 60, 60);
                if(player.overlaps(bossHitbox) && damageCooldown <= 0) {
                    damageCooldown = 1.0f; playerHitTimer = 0.2f;
                    if(hasShield && shieldDurability > 0) {
                        shieldDurability -= 2; slowTimer = 1.0f;
                        if(shieldDurability <= 0) { hasShield = false; equipShield = null; }
                    } else {
                        if(playerArmor > 0) playerArmor -= 2; else playerHp -= 2;
                    }
                }
            }
        }

        // INPUT & MOVEMENT
        float speed = 200 * deltaTime;
        if (hasShield) speed *= 0.7f;
        if (slowTimer > 0) speed *= 0.8f;

        float inputX = 0; float inputY = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) inputY = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) inputY = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) inputX = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) inputX = 1;

        boolean isMoving = (inputX != 0 || inputY != 0);
        if (isMoving) {
            float nextX = player.x + inputX * speed;
            float nextY = player.y + inputY * speed;
            if (!isWall(nextX, player.y, 30)) player.x = nextX;
            if (!isWall(player.x, nextY, 30)) player.y = nextY;
            playerRotation = MathUtils.sin(stateTime * 15) * 10;
        } else {
            playerRotation = 0;
        }

        // AUTO-AIM & CAMERA
        Rectangle targetRect = null;
        if(stageState == StageState.BOSS_ROOM && boss != null) targetRect = boss;
        else {
            Goblin g = findNearestEnemy();
            if(g != null) targetRect = g;
        }

        if (targetRect != null) {
            float pCenterX = player.x + PLAYER_SIZE/2;
            float pCenterY = player.y + PLAYER_SIZE/2;
            facingDir.set((targetRect.x + targetRect.width/2) - pCenterX, (targetRect.y + targetRect.height/2) - pCenterY).nor();
        } else if (isMoving) {
            facingDir.set(inputX, inputY).nor();
        }

        float targetCamX = player.x + PLAYER_SIZE / 2;
        float targetCamY = player.y + PLAYER_SIZE / 2;
        if (targetRect != null) {
            targetCamX += ((targetRect.x + targetRect.width/2) - targetCamX) * 0.3f;
            targetCamY += ((targetRect.y + targetRect.height/2) - targetCamY) * 0.3f;
        }
        camera.position.x += (targetCamX - camera.position.x) * 0.1f;
        camera.position.y += (targetCamY - camera.position.y) * 0.1f;
        camera.update();

        // ATTACK
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (isMelee) {
                attackTimer = 0.2f;
                float reach = 60f;
                float swordX = player.x + PLAYER_SIZE/2 + facingDir.x * reach;
                float swordY = player.y + PLAYER_SIZE/2 + facingDir.y * reach;
                Rectangle attackRect = new Rectangle(swordX - 20, swordY - 20, 40, 40);

                for(Goblin enemy : enemies) {
                    if(attackRect.overlaps(enemy)) {
                        enemy.takeDamage(baseDamage);
                        if (soundHit != null) soundHit.play(0.5f);
                        if (enemy.hp <= 0) enemies.removeValue(enemy, true);
                    }
                }
                if(boss != null && attackRect.overlaps(boss)) {
                    boss.takeDamage(baseDamage);
                    if (soundHit != null) soundHit.play(0.5f);
                }
                if (soundShoot != null) soundShoot.play(0.5f);
            } else {
                float handOffset = 16f;
                float bulletStartX = (facingDir.x >= 0) ? (player.x + PLAYER_SIZE/2 + handOffset) : (player.x + PLAYER_SIZE/2 - handOffset);
                float bulletStartY = player.y + PLAYER_SIZE/2 - 8f;
                bullets.add(new Bullet(bulletStartX, bulletStartY, facingDir.angleDeg()));
                if (soundShoot != null) soundShoot.play(0.5f);
            }
        }
    }

    private void drawGame(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // MAP
        Texture imgFloor = assetManager.get("floor.png", Texture.class);
        Texture imgWall = assetManager.get("bricks.png", Texture.class);
        Texture imgDoor = assetManager.get("door.png", Texture.class);
        for (int row = 0; row < mapHeight; row++) {
            for (int col = 0; col < mapWidth; col++) {
                float x = col * TILE_SIZE;
                float y = (mapHeight - 1 - row) * TILE_SIZE;
                batch.draw(imgFloor, x, y, TILE_SIZE, TILE_SIZE);
                if (mapData[row][col] == 1) batch.draw(imgWall, x, y, TILE_SIZE, TILE_SIZE);
                if (mapData[row][col] == -1) batch.draw(imgDoor, x, y, TILE_SIZE, TILE_SIZE);
            }
        }

        if (stageState == StageState.FIRST_ROOM && !waveSpawned && !pendingSpawnPoints.isEmpty()) {
            float alpha = 0.5f + MathUtils.sin(stateTime * 10) * 0.3f;
            batch.setColor(1, 0, 0, alpha);
            for (Vector2 point : pendingSpawnPoints) {
                batch.draw(imgSpawnMark, point.x - 12, point.y - 12, 64, 64);
            }
            batch.setColor(1, 1, 1, 1);
        }

        Texture imgChestClosed = assetManager.get("chest.png", Texture.class);
        for(Chest chest : lootChests) {
            if (chest.isOpen) {
                batch.draw(imgOpenedChest, chest.rect.x, chest.rect.y, 40, 40);
            } else if (chest.isOpening) {
                chest.stateTime += deltaTime;
                TextureRegion frame = chestOpenAnim.getKeyFrame(chest.stateTime, false);
                batch.draw(frame, chest.rect.x, chest.rect.y, 40, 40);
                if (chestOpenAnim.isAnimationFinished(chest.stateTime)) {
                    chest.isOpen = true; chest.isOpening = false;
                }
            } else {
                batch.draw(imgChestClosed, chest.rect.x, chest.rect.y, 40, 40);
            }
        }

        for(DroppedItem item : droppedItems) {
            if (itemTextures.containsKey(item.type)) {
                batch.draw(itemTextures.get(item.type), item.rect.x, item.rect.y, 30, 30);
            }
        }

        if(stageState == StageState.BOSS_ROOM && boss != null) {
            if(boss.hitTimer > 0) { boss.hitTimer -= deltaTime; batch.setColor(1, 0, 0, 1); }
            Texture imgBoss = assetManager.get("demon.png", Texture.class);
            batch.draw(imgBoss, boss.x, boss.y, boss.width, boss.height);
            batch.setColor(1,1,1,1);
        }

        if (currentState == State.PLAYING) {
            if (damageCooldown > 0) damageCooldown -= deltaTime;
            if (playerHitTimer > 0) playerHitTimer -= deltaTime;

            Texture imgGoblinBody = assetManager.get("goblin faceless.png", Texture.class);

            for (Goblin enemy : enemies) {
                enemy.pathTimer += deltaTime;
                if (enemy.pathTimer > 0.2f) {
                    enemy.pathTimer = 0;
                    enemy.nextStep = findNextStep(enemy.x + 20, enemy.y + 20, player.x + 20, player.y + 20);
                }
                Vector2 dir;
                if (enemy.nextStep != null) {
                    dir = new Vector2(enemy.nextStep.x - (enemy.x + 20), enemy.nextStep.y - (enemy.y + 20)).nor();
                    if (Vector2.dst(enemy.x, enemy.y, enemy.nextStep.x - 20, enemy.nextStep.y - 20) < 5) dir.setZero();
                } else {
                    dir = new Vector2(player.x - enemy.x, player.y - enemy.y).nor();
                }

                float enemySpeed = 100 * deltaTime;
                float moveX = dir.x * enemySpeed;
                float moveY = dir.y * enemySpeed;

                if (!isWall(enemy.x + moveX, enemy.y, 20)) enemy.x += moveX;
                else {
                    float targetY = (enemy.nextStep != null) ? (enemy.nextStep.y - 20) : player.y;
                    float slideDir = (targetY > enemy.y) ? 1 : -1;
                    if (!isWall(enemy.x, enemy.y + slideDir * enemySpeed, 20)) enemy.y += slideDir * enemySpeed;
                }

                if (!isWall(enemy.x, enemy.y + moveY, 20)) enemy.y += moveY;
                else {
                    float targetX = (enemy.nextStep != null) ? (enemy.nextStep.x - 20) : player.x;
                    float slideDir = (targetX > enemy.x) ? 1 : -1;
                    if (!isWall(enemy.x + slideDir * enemySpeed, enemy.y, 20)) enemy.x += slideDir * enemySpeed;
                }

                Rectangle enemyHitbox = new Rectangle(enemy.x + 10, enemy.y + 10, 20, 20);
                if (player.overlaps(enemyHitbox) && damageCooldown <= 0) {
                    damageCooldown = 1.0f;
                    playerHitTimer = 0.2f;
                    if (hasShield && shieldDurability > 0) {
                        shieldDurability--;
                        if(shieldDurability <= 0) { hasShield=false; equipShield=null; }
                    } else {
                        if (playerArmor > 0) playerArmor--; else playerHp--;
                    }
                }

                if (enemy.hitTimer > 0) {
                    enemy.hitTimer -= deltaTime;
                    batch.setColor(1, 0, 0, 1);
                } else batch.setColor(1, 1, 1, 1);
                batch.draw(imgGoblinBody, enemy.x, enemy.y, 40, 40);
                batch.draw(enemy.eyeTexture, enemy.x, enemy.y, 40, 40);
                batch.setColor(1, 1, 1, 1);
            }

            drawPlayer();
            drawWeapon(deltaTime);

            Iterator<Bullet> iter = bullets.iterator();
            while (iter.hasNext()) {
                Bullet b = iter.next();
                b.update(deltaTime);
                if (isCellBlocked(b.x, b.y)) {
                    iter.remove();
                    continue;
                }

                Texture imgArrow = assetManager.get("iron sword.png", Texture.class);
                batch.draw(new TextureRegion(imgArrow), b.x - 10, b.y - 10, 10, 10, 20, 20, 1, 1, b.angle - 45);

                Rectangle bRect = new Rectangle(b.x - 10, b.y - 10, 20, 20);
                boolean hit = false;
                Iterator<Goblin> eIter = enemies.iterator();
                while (eIter.hasNext()) {
                    Goblin enemy = eIter.next();
                    if (bRect.overlaps(enemy)) {
                        enemy.takeDamage(baseDamage);
                        if (enemy.hp <= 0) {
                            eIter.remove();
                            if (soundHit != null) soundHit.play(0.5f);
                        }
                        hit = true; break;
                    }
                }
                if (!hit && boss != null && bRect.overlaps(boss)) {
                    boss.takeDamage(baseDamage);
                    if(soundHit != null) soundHit.play(0.5f);
                    hit = true;
                }

                if (hit || b.lifeTime > 2) iter.remove();
            }
        }

        // UI
        batch.setProjectionMatrix(uiStage.getViewport().getCamera().combined);

        font.setColor(Color.WHITE);
        font.draw(batch, "HP: " + playerHp + "/" + maxHp, 10, 470);
        batch.setColor(Color.RED);
        batch.draw(imgBlank, 10, 450, 20 * playerHp, 10);

        if (maxArmor > 0) {
            font.draw(batch, "Armor: " + playerArmor, 10, 440);
            batch.setColor(Color.CYAN);
            batch.draw(imgBlank, 10, 420, 20 * playerArmor, 10);
        }

        if(stageState == StageState.BOSS_ROOM && boss != null && boss.hp > 0) {
            float barW = 300; float barX = (800 - barW) / 2;
            font.setColor(Color.RED);
            font.draw(batch, "THE DEMON", barX, 475);
            batch.setColor(Color.DARK_GRAY);
            batch.draw(imgBlank, barX, 450, barW, 10);
            batch.setColor(Color.RED);
            batch.draw(imgBlank, barX, 450, barW * ((float)boss.hp / boss.maxHp), 10);
        }

        for(int i=0; i<inventory.size; i++) {
            Texture t = itemTextures.get(inventory.get(i));
            if(t != null) {
                batch.setColor(1,1,1,1);
                batch.draw(t, 10 + i * 40, 10, 32, 32);
                font.draw(batch, (i+1)+"", 10 + i * 40, 50);
            }
        }
        batch.setColor(1, 1, 1, 1);

        batch.end();
    }

    private void drawPlayer() {
        if (playerHitTimer > 0) batch.setColor(1, 0, 0, 1);
        else if (damageCooldown > 0 && MathUtils.sin(stateTime * 20) > 0) batch.setColor(1, 1, 1, 0.5f);
        else {
            if (equipArmor == ItemType.ARMOR_WINDS) batch.setColor(0.8f, 1f, 0.8f, 1);
            else if (equipArmor == ItemType.ARMOR) batch.setColor(0.8f, 0.8f, 1f, 1);
            else batch.setColor(1, 1, 1, 1);
        }

        Texture imgPlayer = assetManager.get("knight.png", Texture.class);
        TextureRegion playerFrame = new TextureRegion(imgPlayer);
        boolean flip = facingDir.x < 0;
        if (flip && !playerFrame.isFlipX()) playerFrame.flip(true, false);
        if (!flip && playerFrame.isFlipX()) playerFrame.flip(true, false);

        batch.draw(playerFrame, player.x, player.y, PLAYER_SIZE / 2, PLAYER_SIZE / 2, PLAYER_SIZE, PLAYER_SIZE, 1, 1 + MathUtils.sin(stateTime * 5) * 0.05f, playerRotation);

        if (equipHelmet != null && itemTextures.containsKey(equipHelmet)) {
            TextureRegion helmReg = new TextureRegion(itemTextures.get(equipHelmet));
            if (flip) helmReg.flip(true, false);
            batch.draw(helmReg, player.x, player.y + 12, PLAYER_SIZE/2, PLAYER_SIZE/2 - 12, PLAYER_SIZE, PLAYER_SIZE, 1, 1, playerRotation);
        }

        batch.setColor(1, 1, 1, 1);
    }

    private void drawWeapon(float deltaTime) {
        if (attackTimer > 0) attackTimer -= deltaTime;

        boolean isSword = (equipWeapon == ItemType.IRON_SWORD);
        Texture texW = isSword ? assetManager.get("iron sword.png", Texture.class) : assetManager.get("bow.png", Texture.class);

        float handOffsetX = 16f;
        float handOffsetY = -8f;
        float baseRotation = isSword ? 0 : -45f;

        if (isSword && attackTimer > 0) {
            float progress = 1 - (attackTimer / 0.2f);
            if (facingDir.x >= 0) baseRotation -= progress * 90;
            else baseRotation += progress * 90;
        }

        float weaponX, weaponY, weaponRotation;
        boolean flipX = facingDir.x < 0;

        if (!flipX) {
            weaponX = (player.x + PLAYER_SIZE / 2) + handOffsetX;
            weaponY = (player.y + PLAYER_SIZE / 2) + handOffsetY;
            weaponRotation = baseRotation;
        } else {
            weaponX = (player.x + PLAYER_SIZE / 2) - handOffsetX;
            weaponY = (player.y + PLAYER_SIZE / 2) + handOffsetY;
            weaponRotation = -baseRotation;
        }

        if (equipShield != null && itemTextures.containsKey(equipShield)) {
            float shieldOffX = flipX ? 12 : -12;
            TextureRegion shieldReg = new TextureRegion(itemTextures.get(equipShield));
            if (flipX) shieldReg.flip(true, false);
            batch.draw(shieldReg, player.x + PLAYER_SIZE/2 + shieldOffX - 10, player.y + PLAYER_SIZE/2 - 10, 10, 10, 20, 20, 1, 1, 0);
        }

        TextureRegion wRegion = new TextureRegion(texW);
        if (flipX) wRegion.flip(true, false);
        batch.draw(wRegion, weaponX - 16, weaponY - 16, 16, 16, 32, 32, 1, 1, weaponRotation);
    }

    @Override
    public void dispose() {
        batch.dispose();
        imgBlank.dispose();
        imgSpawnMark.dispose();
        assetManager.dispose();
        uiStage.dispose(); // Dispose UI Stage
        skin.dispose();
        shapeRenderer.dispose();
    }

    // --- HELPER METHODS ---

    private Goblin findNearestEnemy() {
        Goblin nearest = null; float minDistance = DETECTION_RADIUS;
        float pX = player.x + PLAYER_SIZE/2; float pY = player.y + PLAYER_SIZE/2;
        for (Goblin enemy : enemies) {
            float dist = Vector2.dst(pX, pY, enemy.x + enemy.width/2, enemy.y + enemy.height/2);
            if (dist < minDistance) { minDistance = dist; nearest = enemy; }
        }
        return nearest;
    }

    private boolean isWall(float x, float y, float size) {
        float offset = (PLAYER_SIZE - size) / 2;
        return isCellBlocked(x + offset, y + offset) || isCellBlocked(x + offset + size, y + offset) ||
            isCellBlocked(x + offset, y + offset + size) || isCellBlocked(x + offset + size, y + offset + size);
    }

    private boolean isCellBlocked(float x, float y) {
        int col = (int) (x / TILE_SIZE); int row = mapHeight - 1 - (int) (y / TILE_SIZE);
        if (col < 0 || col >= mapWidth || row < 0 || row >= mapHeight) return true;
        return mapData[row][col] == 1;
    }

    private Vector2 findNextStep(float startX, float startY, float targetX, float targetY) {
        int startCol = (int) (startX / TILE_SIZE);
        int startRow = mapHeight - 1 - (int) (startY / TILE_SIZE);
        int targetCol = (int) (targetX / TILE_SIZE);
        int targetRow = mapHeight - 1 - (int) (targetY / TILE_SIZE);

        if (startCol == targetCol && startRow == targetRow) return null;

        Array<PathNode> queue = new Array<>();
        boolean[][] visited = new boolean[mapHeight][mapWidth];

        queue.add(new PathNode(startCol, startRow, null));
        visited[startRow][startCol] = true;
        PathNode targetNode = null;
        int loopCount = 0;

        while (queue.size > 0 && loopCount < 500) {
            PathNode current = queue.removeIndex(0);
            loopCount++;
            if (current.x == targetCol && current.y == targetRow) { targetNode = current; break; }
            int[] dRow = {-1, 1, 0, 0}; int[] dCol = {0, 0, -1, 1};
            for (int i = 0; i < 4; i++) {
                int newRow = current.y + dRow[i]; int newCol = current.x + dCol[i];
                if (newRow >= 0 && newRow < mapHeight && newCol >= 0 && newCol < mapWidth
                    && !visited[newRow][newCol] && mapData[newRow][newCol] == 0) {
                    visited[newRow][newCol] = true;
                    queue.add(new PathNode(newCol, newRow, current));
                }
            }
        }
        if (targetNode != null) {
            PathNode step = targetNode;
            while (step.parent != null && step.parent.parent != null) step = step.parent;
            return new Vector2(step.x * TILE_SIZE + TILE_SIZE/2, (mapHeight - 1 - step.y) * TILE_SIZE + TILE_SIZE/2);
        }
        return null;
    }

    class Goblin extends Rectangle {
        Texture eyeTexture; int hp, maxHp; float hitTimer, pathTimer; Vector2 nextStep;
        public Goblin(float x, float y, float size, Texture eye) { super(x, y, size, size); eyeTexture = eye; maxHp = 3; hp = 3; }
        public void takeDamage(int d) { hp -= d; hitTimer = 0.1f; }
    }

    // BOSS CLASS
    class DemonBoss extends Rectangle {
        int hp = 50, maxHp = 50;
        float hitTimer = 0;
        public DemonBoss(float x, float y, float size) { super(x, y, size, size); }
        public void takeDamage(int d) { hp -= d; hitTimer = 0.1f; }

        public void update(float dt, Rectangle player) {
            Vector2 dir = new Vector2(player.x - x, player.y - y).nor();
            float speed = 70 * dt; // Boss đi chậm hơn
            x += dir.x * speed;
            y += dir.y * speed;
        }
    }

    class Bullet {
        float x, y, vx, vy, angle, lifeTime;
        public Bullet(float x, float y, float angle) {
            this.x = x; this.y = y; this.angle = angle;
            this.vx = MathUtils.cosDeg(angle) * 600; this.vy = MathUtils.sinDeg(angle) * 600;
        }
        public void update(float dt) { x += vx * dt; y += vy * dt; lifeTime += dt; }
    }
    class PathNode { int x, y; PathNode parent; public PathNode(int x, int y, PathNode p) { this.x=x; this.y=y; this.parent=p; }}

    class Chest {
        float x, y; Rectangle rect;
        boolean isOpen = false;
        boolean isOpening = false;
        float stateTime = 0;
        int id;
        public Chest(float x, float y) { this.x=x; this.y=y; this.rect = new Rectangle(x, y, 40, 40); }
    }
    class DroppedItem {
        float x, y; ItemType type; Rectangle rect;
        public DroppedItem(float x, float y, ItemType t) { this.x=x; this.y=y; this.type=t; this.rect=new Rectangle(x, y, 30, 30); }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        if (currentState == State.PLAYING) {
            currentState = State.PAUSED;
            uiStage.clear(); // Clear old buttons
            createPauseMenu(uiStage); // Add buttons to UI Stage
            multiplexer.addProcessor(uiStage);
            Gdx.input.setInputProcessor(multiplexer);
        }
    }

    @Override
    public void resume() {
        if (currentState == State.PAUSED) {
            currentState = State.PLAYING;
            uiStage.clear(); // Clear buttons when resuming
            multiplexer.removeProcessor(uiStage);
            Gdx.input.setInputProcessor(multiplexer);
        }
    }

    private void drawPauseOverlay() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(uiStage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.5f));
        shapeRenderer.rect(0, 0, 800, 480);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void createPauseMenu() {
        createPauseMenu(uiStage);
    }

    private void createPauseMenu(Stage stage) {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton resumeButton = new TextButton("Resume", skin);
        TextButton quitButton = new TextButton("Quit", skin);

        table.add(resumeButton).pad(10).row();
        table.add(quitButton).pad(10).row();

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) { resume(); }
        });
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) { Gdx.app.exit(); }
        });
    }

    private void createVictoryMenu() {
        uiStage.clear();
        Table table = new Table();
        table.setFillParent(true);
        uiStage.addActor(table);

        TextButton menuButton = new TextButton("Return to Menu", skin);
        table.add(menuButton).pad(20);

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentState = State.MENU;
                uiStage.clear();
                multiplexer.removeProcessor(uiStage);
            }
        });

        multiplexer.addProcessor(uiStage);
    }
}
