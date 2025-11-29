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

    enum ItemType {
        HELMET_WINDS, ARMOR_WINDS, CRUSADER_SHIELD, PENDULUM, IRON_SWORD, BOW,
        HEALTH_POTION, CHOCOLATE_CAKE, APPLE,
        HELMET, ARMOR, BARE_SHIELD, GOLDEN_BOW
    }

    final int TILE_SIZE = 64;
    final float PLAYER_SIZE = 40;
    final float DETECTION_RADIUS = 300f;

    // MAP DATA (Giữ nguyên, có cửa -1)
    int[][] map1 = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, -1}, // Cửa ra
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
        {0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, -1}, // Cửa ra
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
        {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // Cửa vào (Sẽ tự mở khi map load xong)
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

    Rectangle player;
    Vector2 facingDir;
    float stateTime = 0f;
    float playerRotation = 0f;

    int playerHp = 5, maxHp = 5;
    int playerArmor = 0, maxArmor = 0;
    int baseDamage = 1;
    float playerHitTimer = 0, damageCooldown = 0;
    float attackTimer = 0;

    ItemType equipArmor = null, equipHelmet = null, equipShield = null, equipWeapon = ItemType.BOW;
    boolean isMelee = false, hasPendulum = false, hasShield = false;
    int shieldDurability = 5;

    Array<ItemType> inventory;
    Array<DroppedItem> droppedItems;
    Array<Bullet> bullets;
    Array<Goblin> enemies;
    Array<Chest> lootChests = new Array<>();
    DemonBoss boss;

    enum StageState { FIRST_ROOM, CHEST_ROOM, BOSS_ROOM }
    StageState stageState = StageState.FIRST_ROOM;

    int chestRoomStartX = MAP1_WIDTH * TILE_SIZE;
    int bossRoomStartX  = (MAP1_WIDTH + MAP2_WIDTH) * TILE_SIZE;

    Sound soundShoot, soundHit;
    Texture imgBlank, imgSpawnMark;
    java.util.Map<ItemType, Texture> itemTextures;
    Animation<TextureRegion> chestOpenAnim;
    Texture imgOpenedChest;

    private Stage uiStage;
    private Skin skin;
    private ShapeRenderer shapeRenderer;
    private InputMultiplexer multiplexer;

    float spawnDelayTimer = 0f;
    float spawnDelayDuration = 2.0f;
    boolean waveSpawned = false;
    Array<Vector2> pendingSpawnPoints = new Array<>();
    boolean areSpawnPointsGenerated = false;
    float slowTimer = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camera);
        viewport.apply();
        font = new BitmapFont();

        assetManager = new AssetManager();
        // --- LOAD ASSETS (Dùng phương pháp an toàn) ---
        String[] files = {
            "knight.png", "floor.png", "bricks.png", "goblin faceless.png",
            "eyes.png", "big eyes.png", "evil eyes.png",
            "iron sword.png", "bow.png", "door.png", "demon.png",
            "chest.png", "opened chest.png", "chest opening animation.png",
            "helmet of the winds.png", "armor of the winds.png", "crusader's shield.png",
            "pendulum.png", "health potion.png", "chocolate cake.png", "apple.png",
            "helmet.png", "armor.png", "bare shield.png", "shoot.wav", "hit.wav"
        };

        for(String f : files) {
            if(Gdx.files.internal(f).exists()) {
                if(f.endsWith(".wav")) assetManager.load(f, Sound.class);
                else assetManager.load(f, Texture.class);
            }
        }
        assetManager.finishLoading();

        if (assetManager.isLoaded("shoot.wav")) soundShoot = assetManager.get("shoot.wav", Sound.class);
        if (assetManager.isLoaded("hit.wav")) soundHit = assetManager.get("hit.wav", Sound.class);

        // Animation Setup
        if (assetManager.isLoaded("chest opening animation.png")) {
            Texture sheet = assetManager.get("chest opening animation.png");
            TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth()/6, sheet.getHeight());
            chestOpenAnim = new Animation<>(0.1f, tmp[0]);
        } else {
            // Fallback
            Texture t = assetManager.get("chest.png");
            chestOpenAnim = new Animation<>(0.1f, new TextureRegion(t));
        }
        imgOpenedChest = assetManager.isLoaded("opened chest.png") ? assetManager.get("opened chest.png") : assetManager.get("chest.png");

        // Init Map Items
        itemTextures = new java.util.HashMap<>();
        // Helper load item textures... (tương tự code cũ)
        loadItemTex(ItemType.HELMET_WINDS, "helmet of the winds.png");
        loadItemTex(ItemType.ARMOR_WINDS, "armor of the winds.png");
        loadItemTex(ItemType.CRUSADER_SHIELD, "crusader's shield.png");
        loadItemTex(ItemType.PENDULUM, "pendulum.png");
        loadItemTex(ItemType.IRON_SWORD, "iron sword.png");
        loadItemTex(ItemType.BOW, "bow.png");
        loadItemTex(ItemType.HEALTH_POTION, "health potion.png");
        loadItemTex(ItemType.CHOCOLATE_CAKE, "chocolate cake.png");
        loadItemTex(ItemType.APPLE, "apple.png");
        loadItemTex(ItemType.HELMET, "helmet.png");
        loadItemTex(ItemType.ARMOR, "armor.png");
        loadItemTex(ItemType.BARE_SHIELD, "bare shield.png");

        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE); pixmap.fill();
        imgBlank = new Texture(pixmap);

        com.badlogic.gdx.graphics.Pixmap pMark = new com.badlogic.gdx.graphics.Pixmap(64, 64, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pMark.setColor(new Color(1, 0, 0, 0.5f)); pMark.fillCircle(32, 32, 30);
        imgSpawnMark = new Texture(pMark);
        pixmap.dispose(); pMark.dispose();

        // --- SETUP UI & INPUT ---
        shapeRenderer = new ShapeRenderer();
        uiStage = new Stage(new FitViewport(800, 480)); // Viewport chuẩn
        try { skin = new Skin(Gdx.files.internal("uiskin.json")); } catch (Exception e) {}

        // KHỞI TẠO BỘ QUẢN LÝ INPUT ĐA LUỒNG
        multiplexer = new InputMultiplexer();

        // 1. Ưu tiên UI Stage (Để nút bấm nhận được click chuột trước tiên)
        multiplexer.addProcessor(uiStage);

        // 2. Sau đó mới đến Phím tắt (ESC, Số 1-5)
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    if (currentState == State.PLAYING) pause();
                    else if (currentState == State.PAUSED) resume();
                    return true;
                }
                // Chỉ dùng item khi đang chơi
                if (currentState == State.PLAYING && keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_5) {
                    useItem(keycode - Input.Keys.NUM_1);
                    return true;
                }
                return false;
            }
        });

        // Kích hoạt ngay lập tức
        Gdx.input.setInputProcessor(multiplexer);

        copyMapsIntoMapData();
        resetGame();
    }

    private void loadItemTex(ItemType type, String path) {
        if(assetManager.isLoaded(path)) itemTextures.put(type, assetManager.get(path));
    }

    private void copyMapsIntoMapData() {
        for (int row = 0; row < MAP_HEIGHT; row++) {
            for (int col = 0; col < MAP1_WIDTH; col++) mapData[row][col] = map1[row][col];
            for (int col = 0; col < MAP2_WIDTH; col++) mapData[row][MAP1_WIDTH + col] = map2[row][col];
            for (int col = 0; col < MAP3_WIDTH; col++) mapData[row][MAP1_WIDTH + MAP2_WIDTH + col] = map3[row][col];
        }
        // Mở cửa phòng 3 (vì logic đi từ 2 sang 3 sẽ tự động đặt người chơi qua cửa)
        openDoor(MAP1_WIDTH + MAP2_WIDTH);
    }

    // [NEW] Hàm mở cửa (Xóa tường)
    private void openDoor(int colIndex) {
        for(int row=0; row<MAP_HEIGHT; row++) {
            if(mapData[row][colIndex] == -1) {
                mapData[row][colIndex] = 0; // Biến thành sàn nhà
            }
        }
    }

    private void resetGame() {
        player = new Rectangle(150, 150, PLAYER_SIZE, PLAYER_SIZE);
        facingDir = new Vector2(1, 0);
        playerHp = 5; maxHp = 5; playerArmor = 0; maxArmor = 0; baseDamage = 1;

        equipArmor = null; equipHelmet = null; equipShield = null; equipWeapon = ItemType.BOW;
        shieldDurability = 5;
        hasShield = false; hasPendulum = false; isMelee = false;

        if (bullets == null) bullets = new Array<>(); else bullets.clear();
        if (enemies == null) enemies = new Array<>(); else enemies.clear();
        if (lootChests == null) lootChests = new Array<>(); else lootChests.clear();
        if (droppedItems == null) droppedItems = new Array<>(); else droppedItems.clear();
        if (inventory == null) inventory = new Array<>(); else inventory.clear();

        spawnDelayTimer = 0f; waveSpawned = false; areSpawnPointsGenerated = false;
        if (pendingSpawnPoints == null) pendingSpawnPoints = new Array<>(); else pendingSpawnPoints.clear();

        stageState = StageState.FIRST_ROOM;
        boss = null;

        // Reset lại map (đóng cửa)
        copyMapsIntoMapData();
    }

    // ... (Các hàm spawnEnemy, spawnChest, useItem giữ nguyên) ...
    private void generateSpawnPoints(int count) {
        pendingSpawnPoints.clear();
        for (int i = 0; i < count; i++) {
            float x, y; int s = 0;
            do {
                x = MathUtils.random(TILE_SIZE, (MAP1_WIDTH - 1) * TILE_SIZE);
                y = MathUtils.random(TILE_SIZE, (MAP_HEIGHT - 1) * TILE_SIZE);
                s++;
            } while (isWall(x, y, 40) && s < 100);
            pendingSpawnPoints.add(new Vector2(x, y));
        }
        areSpawnPointsGenerated = true;
    }
    private void spawnEnemyAt(float x, float y) {
        String[] eyes = {"eyes.png", "big eyes.png", "evil eyes.png"};
        Texture randomEye = assetManager.get(eyes[MathUtils.random(0, 2)], Texture.class);
        enemies.add(new Goblin(x, y, 40, randomEye));
    }
    private void spawnBoss() { boss = new DemonBoss(bossRoomStartX + 460, 260 , 100); }
    private void spawnChest(int id) {
        float x, y; int s=0; Rectangle newChest;
        do {
            x = MathUtils.random(chestRoomStartX + TILE_SIZE, bossRoomStartX - TILE_SIZE);
            y = MathUtils.random(TILE_SIZE, MAP_HEIGHT * TILE_SIZE - TILE_SIZE);
            newChest = new Rectangle(x, y, 40, 40); s++;
        } while ((isWall(x, y, 40) || overlapsChest(newChest)) && s < 100);
        Chest c = new Chest(x, y); c.id = id; lootChests.add(c);
    }
    private boolean overlapsChest(Rectangle chest) {
        for (Chest c : lootChests) if (c.rect.overlaps(chest)) return true; return false;
    }

    // ... (Logic useItem, triggerChestOpen, pickUpItem giữ nguyên như cũ) ...
    private void useItem(int index) {
        if (index >= 0 && index < inventory.size) {
            ItemType item = inventory.get(index);
            boolean used = false;
            if (item == ItemType.HEALTH_POTION) { if(playerHp < maxHp) { playerHp = Math.min(playerHp + 3, maxHp); used = true; } }
            else if (item == ItemType.CHOCOLATE_CAKE) { if(playerHp < maxHp) { playerHp = Math.min(playerHp + 2, maxHp); used = true; } }
            else if (item == ItemType.APPLE) { if(playerHp < maxHp) { playerHp = Math.min(playerHp + 1, maxHp); used = true; } }
            if (used) inventory.removeIndex(index);
        }
    }
    private void triggerChestOpen(Chest chest) {
        chest.isOpening = true;

        // Tìm vị trí rơi đồ hợp lý (không phải tường)
        float dropX = chest.rect.x;
        float dropY = chest.rect.y;

        // Thử rơi xuống dưới
        if (!isWall(dropX, dropY - 64, 30)) dropY -= 64;
            // Thử rơi lên trên
        else if (!isWall(dropX, dropY + 64, 30)) dropY += 64;
            // Thử sang trái
        else if (!isWall(dropX - 64, dropY, 30)) dropX -= 64;
            // Thử sang phải
        else if (!isWall(dropX + 64, dropY, 30)) dropX += 64;

        // DROP THEO ID CỦA RƯƠNG
        switch (chest.id) {
            case 0: dropItem(ItemType.HELMET_WINDS, dropX, dropY); break;
            case 1: dropItem(ItemType.ARMOR_WINDS, dropX, dropY); break;
            case 2: dropItem(ItemType.IRON_SWORD, dropX, dropY); break;
            case 3: dropItem(ItemType.CRUSADER_SHIELD, dropX, dropY); break;
            case 4:
                dropItem(ItemType.HEALTH_POTION, dropX, dropY);
                // Mẹo: Rải 3 món ra xung quanh nếu là gói tiêu hao
                if (!isWall(dropX + 30, dropY, 30)) dropItem(ItemType.CHOCOLATE_CAKE, dropX + 30, dropY);
                if (!isWall(dropX - 30, dropY, 30)) dropItem(ItemType.APPLE, dropX - 30, dropY);
                break;
            case 5: dropItem(ItemType.PENDULUM, dropX, dropY); break;
        }
    }
    private void dropItem(ItemType type, float x, float y) { droppedItems.add(new DroppedItem(x, y, type)); }
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
            case MENU: updateMenu(); break;
            case PLAYING:
                updateGameLogic(deltaTime);
                drawGame(deltaTime);
                if (playerHp <= 0) {
                    currentState = State.GAME_OVER;
                    camera.position.set(viewport.getWorldWidth()/2f, viewport.getWorldHeight()/2f, 0); camera.update();
                }
                if (stageState == StageState.BOSS_ROOM && boss != null && boss.hp <= 0) {
                    currentState = State.VICTORY; createVictoryMenu(); Gdx.input.setInputProcessor(multiplexer);
                }
                break;
            case GAME_OVER: updateGameOver(); break;
            case VICTORY: drawGame(0); drawPauseOverlay(); uiStage.getViewport().apply(); uiStage.act(deltaTime); uiStage.draw(); break;
            case PAUSED: drawGame(0); drawPauseOverlay(); uiStage.getViewport().apply(); uiStage.act(deltaTime); uiStage.draw(); break;
        }
    }

    // Update Menu & GameOver giữ nguyên
    private void updateMenu() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) currentState = State.PLAYING;
        batch.begin(); font.getData().setScale(2); font.setColor(Color.YELLOW);
        font.draw(batch, "DUNGEON SURVIVOR", 250, 300); font.getData().setScale(1.5f); font.setColor(Color.WHITE);
        if ((System.currentTimeMillis()/500)%2==0) font.draw(batch, "Press ENTER to Start", 280, 200);
        batch.end();
    }
    private void updateGameOver() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) { resetGame(); currentState = State.PLAYING; }
        batch.begin(); font.getData().setScale(2); font.setColor(Color.RED); font.draw(batch, "GAME OVER", 320, 300);
        font.getData().setScale(1.5f); font.setColor(Color.WHITE); font.draw(batch, "Press ENTER to Restart", 280, 200); batch.end();
    }

    // --- LOGIC UPDATE CHÍNH ---
    private void updateGameLogic(float deltaTime) {
        stateTime += deltaTime;
        if (slowTimer > 0) slowTimer -= deltaTime;

        // Giới hạn di chuyển (Tường vô hình)
        float currentRoomLimitX = 0;

        // --- PHÒNG 1 ---
        if (stageState == StageState.FIRST_ROOM) {
            currentRoomLimitX = chestRoomStartX; // Chặn ở cửa

            // Spawn
            if (!waveSpawned) {
                if (!areSpawnPointsGenerated) generateSpawnPoints(5);
                spawnDelayTimer += deltaTime;
                if (spawnDelayTimer >= spawnDelayDuration) {
                    for(Vector2 point : pendingSpawnPoints) spawnEnemyAt(point.x, point.y);
                    pendingSpawnPoints.clear(); waveSpawned = true;
                }
            }

            // Check Clear
            if (waveSpawned && enemies.isEmpty()) {
                openDoor(MAP1_WIDTH - 1); // Mở cửa (Xóa tường)
                currentRoomLimitX += 128; // Cho phép đi qua cửa

                // Nếu đi qua cửa -> Sang phòng 2
                if (player.x > chestRoomStartX + 32) {
                    stageState = StageState.CHEST_ROOM;
                    player.x = chestRoomStartX + 80; // Đẩy sang phòng 2
                    for(int i=0; i<6; i++) spawnChest(i); // Spawn rương
                }
            }
        }
        // --- PHÒNG 2 ---
        else if (stageState == StageState.CHEST_ROOM) {
            currentRoomLimitX = bossRoomStartX; // Chặn ở cửa 2

            for(Chest c : lootChests) if(!c.isOpen && !c.isOpening && player.overlaps(c.rect)) triggerChestOpen(c);
            Iterator<DroppedItem> it = droppedItems.iterator();
            while(it.hasNext()){ DroppedItem item = it.next(); if(player.overlaps(item.rect)) pickUpItem(item); }

            boolean allLooted = true;
            for(Chest c : lootChests) if(!c.isOpen) allLooted = false;
            if(!droppedItems.isEmpty()) allLooted = false;

            if (allLooted) {
                openDoor(MAP1_WIDTH + MAP2_WIDTH - 1); // Mở cửa 2
                currentRoomLimitX += 128;

                if (player.x > bossRoomStartX + 32) {
                    stageState = StageState.BOSS_ROOM;
                    player.x = bossRoomStartX + 80;
                    spawnBoss();
                }
            }
        }
        // --- PHÒNG 3 ---
        else if (stageState == StageState.BOSS_ROOM) {
            currentRoomLimitX = mapWidth * TILE_SIZE;
            if (boss != null) {
                boss.update(deltaTime, player);
                Rectangle bossHitbox = new Rectangle(boss.x+20, boss.y+20, 60, 60);
                if(player.overlaps(bossHitbox) && damageCooldown <= 0) {
                    damageCooldown = 1.0f; playerHitTimer = 0.2f;
                    if(hasShield && shieldDurability > 0) {
                        shieldDurability -= 2; slowTimer = 1.0f;
                        if(shieldDurability <= 0) { hasShield = false; equipShield = null; }
                    } else { if(playerArmor > 0) playerArmor -= 2; else playerHp -= 2; }
                }
            }
        }

        // --- INPUT & MOVEMENT ---
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

            // Check Tường (isWall) & Giới hạn phòng (LimitX)
            if (!isWall(nextX, player.y, 30)) {
                if (nextX < currentRoomLimitX - PLAYER_SIZE) { // Invisible Wall Check
                    player.x = nextX;
                }
            }
            if (!isWall(player.x, nextY, 30)) player.y = nextY;
            playerRotation = MathUtils.sin(stateTime * 15) * 10;
        } else { playerRotation = 0; }

        // AUTO-AIM & CAM
        Rectangle targetRect = null;
        if(stageState == StageState.BOSS_ROOM && boss != null) targetRect = boss;
        else { Goblin g = findNearestEnemy(); if(g != null) targetRect = g; }

        if (targetRect != null) {
            facingDir.set((targetRect.x + targetRect.width/2) - (player.x+PLAYER_SIZE/2), (targetRect.y + targetRect.height/2) - (player.y+PLAYER_SIZE/2)).nor();
        } else if (isMoving) { facingDir.set(inputX, inputY).nor(); }

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
                attackTimer = 0.2f; float reach = 60f;
                float sx = player.x + PLAYER_SIZE/2 + facingDir.x * reach;
                float sy = player.y + PLAYER_SIZE/2 + facingDir.y * reach;
                Rectangle attackRect = new Rectangle(sx - 20, sy - 20, 40, 40);
                for(Goblin enemy : enemies) {
                    if(attackRect.overlaps(enemy)) {
                        enemy.takeDamage(baseDamage);
                        if (soundHit != null) soundHit.play(0.5f);
                        if (enemy.hp <= 0) enemies.removeValue(enemy, true);
                    }
                }
                if(boss != null && attackRect.overlaps(boss)) {
                    boss.takeDamage(baseDamage); if (soundHit != null) soundHit.play(0.5f);
                }
                if (soundShoot != null) soundShoot.play(0.5f);
            } else {
                float handOffset = 16f;
                float bx = (facingDir.x >= 0) ? (player.x + PLAYER_SIZE/2 + handOffset) : (player.x + PLAYER_SIZE/2 - handOffset);
                float by = player.y + PLAYER_SIZE/2 - 8f;
                bullets.add(new Bullet(bx, by, facingDir.angleDeg()));
                if (soundShoot != null) soundShoot.play(0.5f);
            }
        }
    }

    // ... DrawGame giữ nguyên ...
    private void drawGame(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // MAP
        Texture imgFloor = assetManager.get("floor.png"); Texture imgWall = assetManager.get("bricks.png"); Texture imgDoor = assetManager.get("door.png");
        for (int row = 0; row < mapHeight; row++) {
            for (int col = 0; col < mapWidth; col++) {
                float x = col * TILE_SIZE; float y = (mapHeight - 1 - row) * TILE_SIZE;
                batch.draw(imgFloor, x, y, TILE_SIZE, TILE_SIZE);
                if (mapData[row][col] == 1) batch.draw(imgWall, x, y, TILE_SIZE, TILE_SIZE);
                if (mapData[row][col] == -1) batch.draw(imgDoor, x, y, TILE_SIZE, TILE_SIZE);
            }
        }
        // SPAWN MARK
        if (stageState == StageState.FIRST_ROOM && !waveSpawned && !pendingSpawnPoints.isEmpty()) {
            float alpha = 0.5f + MathUtils.sin(stateTime * 10) * 0.3f;
            batch.setColor(1, 0, 0, alpha);
            for (Vector2 point : pendingSpawnPoints) batch.draw(imgSpawnMark, point.x - 12, point.y - 12, 64, 64);
            batch.setColor(1, 1, 1, 1);
        }
        // CHESTS
        Texture imgChestClosed = assetManager.get("chest.png");
        for(Chest chest : lootChests) {
            if (chest.isOpen) batch.draw(imgOpenedChest, chest.rect.x, chest.rect.y, 40, 40);
            else if (chest.isOpening) {
                chest.stateTime += deltaTime;
                TextureRegion frame = chestOpenAnim.getKeyFrame(chest.stateTime, false);
                batch.draw(frame, chest.rect.x, chest.rect.y, 40, 40);
                if (chestOpenAnim.isAnimationFinished(chest.stateTime)) { chest.isOpen = true; chest.isOpening = false; }
            } else batch.draw(imgChestClosed, chest.rect.x, chest.rect.y, 40, 40);
        }
        // DROPS
        for(DroppedItem item : droppedItems) if (itemTextures.containsKey(item.type)) batch.draw(itemTextures.get(item.type), item.rect.x, item.rect.y, 30, 30);
        // BOSS
        if(stageState == StageState.BOSS_ROOM && boss != null) {
            if(boss.hitTimer > 0) { boss.hitTimer -= deltaTime; batch.setColor(1, 0, 0, 1); }
            batch.draw((Texture)assetManager.get("demon.png"), boss.x, boss.y, boss.width, boss.height);
            batch.setColor(1,1,1,1);
        }
        // PLAYING ENTITIES
        if (currentState == State.PLAYING) {
            if (damageCooldown > 0) damageCooldown -= deltaTime;
            if (playerHitTimer > 0) playerHitTimer -= deltaTime;
            Texture imgGoblinBody = assetManager.get("goblin faceless.png");
            for (Goblin enemy : enemies) {
                // AI Update & Draw... (Rút gọn để paste)
                enemy.pathTimer += deltaTime;
                if(enemy.pathTimer>0.2f){ enemy.pathTimer=0; enemy.nextStep=findNextStep(enemy.x+20,enemy.y+20,player.x+20,player.y+20); }
                Vector2 dir = (enemy.nextStep!=null) ? new Vector2(enemy.nextStep.x-(enemy.x+20),enemy.nextStep.y-(enemy.y+20)).nor() : new Vector2(player.x-enemy.x,player.y-enemy.y).nor();
                float es = 100*deltaTime;
                if(!isWall(enemy.x+dir.x*es, enemy.y, 20)) enemy.x+=dir.x*es;
                else { float ty=(enemy.nextStep!=null)?enemy.nextStep.y-20:player.y; float sd=(ty>enemy.y)?1:-1; if(!isWall(enemy.x,enemy.y+sd*es,20)) enemy.y+=sd*es; }
                if(!isWall(enemy.x, enemy.y+dir.y*es, 20)) enemy.y+=dir.y*es;
                else { float tx=(enemy.nextStep!=null)?enemy.nextStep.x-20:player.x; float sd=(tx>enemy.x)?1:-1; if(!isWall(enemy.x+sd*es,enemy.y,20)) enemy.x+=sd*es; }

                Rectangle eh = new Rectangle(enemy.x+10, enemy.y+10, 20, 20);
                if(player.overlaps(eh) && damageCooldown<=0) {
                    damageCooldown=1f; playerHitTimer=0.2f;
                    if(hasShield && shieldDurability>0) { shieldDurability--; if(shieldDurability<=0){hasShield=false; equipShield=null;} }
                    else { if(playerArmor>0) playerArmor--; else playerHp--; }
                }
                if(enemy.hitTimer>0){ enemy.hitTimer-=deltaTime; batch.setColor(1,0,0,1); } else batch.setColor(1,1,1,1);
                batch.draw(imgGoblinBody, enemy.x, enemy.y, 40, 40);
                batch.draw(enemy.eyeTexture, enemy.x, enemy.y, 40, 40);
                batch.setColor(1,1,1,1);
            }
            drawPlayer();
            drawWeapon(deltaTime);
            Iterator<Bullet> iter = bullets.iterator();
            while(iter.hasNext()) {
                Bullet b = iter.next(); b.update(deltaTime);
                if(isCellBlocked(b.x, b.y)) { iter.remove(); continue; }
                batch.draw(new TextureRegion((Texture)assetManager.get("iron sword.png")), b.x-10, b.y-10, 10, 10, 20, 20, 1, 1, b.angle-45);
                Rectangle br = new Rectangle(b.x-10, b.y-10, 20, 20); boolean hit=false;
                Iterator<Goblin> eit = enemies.iterator();
                while(eit.hasNext()){ Goblin g=eit.next(); if(br.overlaps(g)){ g.takeDamage(baseDamage); if(soundHit!=null)soundHit.play(0.5f); if(g.hp<=0)eit.remove(); hit=true; break; } }
                if(!hit && boss!=null && br.overlaps(boss)) { boss.takeDamage(baseDamage); if(soundHit!=null)soundHit.play(0.5f); hit=true; }
                if(hit || b.lifeTime>2) iter.remove();
            }
        }
        // UI
        batch.setProjectionMatrix(uiStage.getViewport().getCamera().combined);
        font.getData().setScale(1.0f);
        batch.setColor(Color.RED); batch.draw(imgBlank, 10, 450, 20 * playerHp, 10);
        if (maxArmor > 0) { batch.setColor(Color.BLUE); batch.draw(imgBlank, 10, 420, 20 * playerArmor, 10); }
        batch.setColor(Color.WHITE);
        font.draw(batch, "HP: " + playerHp + "/" + maxHp, 10, 475);
        if (maxArmor > 0) { font.setColor(Color.CYAN); font.draw(batch, "Armor: " + playerArmor, 10, 445); }
        if(stageState == StageState.BOSS_ROOM && boss != null && boss.hp > 0) {
            float bx=(800-300)/2; font.setColor(Color.RED); font.draw(batch, "THE DEMON", bx, 475);
            batch.setColor(Color.DARK_GRAY); batch.draw(imgBlank, bx, 450, 300, 10);
            batch.setColor(Color.RED); batch.draw(imgBlank, bx, 450, 300*((float)boss.hp/boss.maxHp), 10);
        }
        for(int i=0; i<inventory.size; i++) {
            Texture t=itemTextures.get(inventory.get(i));
            if(t!=null){ batch.setColor(1,1,1,1); batch.draw(t, 10+i*40, 10, 32, 32); font.setColor(Color.WHITE); font.draw(batch, (i+1)+"", 10+i*40, 50); }
        }
        batch.setColor(1,1,1,1);
        batch.end();
    }

    // Helper functions (drawPlayer, drawWeapon, dispose, class definitions...)
    // Copy từ các bài trước vì không thay đổi logic
    private void drawPlayer() {
        if (playerHitTimer > 0) batch.setColor(1, 0, 0, 1);
        else if (damageCooldown > 0 && MathUtils.sin(stateTime * 20) > 0) batch.setColor(1, 1, 1, 0.5f);
        else { if (equipArmor == ItemType.ARMOR_WINDS) batch.setColor(0.8f, 1f, 0.8f, 1); else if (equipArmor == ItemType.ARMOR) batch.setColor(0.8f, 0.8f, 1f, 1); else batch.setColor(1, 1, 1, 1); }
        Texture imgPlayer = assetManager.get("knight.png"); TextureRegion reg = new TextureRegion(imgPlayer);
        boolean flip = facingDir.x < 0; if (flip && !reg.isFlipX()) reg.flip(true, false); if (!flip && reg.isFlipX()) reg.flip(true, false);
        batch.draw(reg, player.x, player.y, PLAYER_SIZE/2, PLAYER_SIZE/2, PLAYER_SIZE, PLAYER_SIZE, 1, 1+MathUtils.sin(stateTime*5)*0.05f, playerRotation);
        if (equipHelmet != null && itemTextures.containsKey(equipHelmet)) { TextureRegion hr = new TextureRegion(itemTextures.get(equipHelmet)); if(flip) hr.flip(true, false); batch.draw(hr, player.x, player.y+12, PLAYER_SIZE/2, PLAYER_SIZE/2-12, PLAYER_SIZE, PLAYER_SIZE, 1, 1, playerRotation); }
        batch.setColor(1, 1, 1, 1);
    }
    private void drawWeapon(float deltaTime) {
        if(attackTimer>0) attackTimer-=deltaTime;
        boolean isSword=(equipWeapon==ItemType.IRON_SWORD);
        Texture tex=(isSword)?assetManager.get("iron sword.png"):assetManager.get("bow.png");
        float offX=16, offY=-8, rot=(isSword)?0:-45f;
        if(isSword && attackTimer>0) { float p=1-(attackTimer/0.2f); if(facingDir.x>=0) rot-=p*90; else rot+=p*90; }
        boolean flip=facingDir.x<0;
        float wx=player.x+PLAYER_SIZE/2+(flip?-offX:offX);
        float wy=player.y+PLAYER_SIZE/2+offY;
        if(equipShield!=null && itemTextures.containsKey(equipShield)){
            TextureRegion sr=new TextureRegion(itemTextures.get(equipShield)); if(flip)sr.flip(true,false);
            batch.draw(sr, player.x+PLAYER_SIZE/2+(flip?12:-12)-10, player.y+PLAYER_SIZE/2-10, 10, 10, 20, 20, 1, 1, 0);
        }
        TextureRegion wr=new TextureRegion(tex); if(flip)wr.flip(true,false);
        batch.draw(wr, wx-16, wy-16, 16, 16, 32, 32, 1, 1, flip?-rot:rot);
    }
    @Override public void dispose() { batch.dispose(); imgBlank.dispose(); imgSpawnMark.dispose(); assetManager.dispose(); uiStage.dispose(); skin.dispose(); shapeRenderer.dispose(); }
    private boolean isWall(float x, float y, float size) { float off=(PLAYER_SIZE-size)/2; return isCellBlocked(x+off, y+off) || isCellBlocked(x+off+size, y+off) || isCellBlocked(x+off, y+off+size) || isCellBlocked(x+off+size, y+off+size); }
    private boolean isCellBlocked(float x, float y) { int c=(int)(x/TILE_SIZE), r=mapHeight-1-(int)(y/TILE_SIZE); if(c<0||c>=mapWidth||r<0||r>=mapHeight)return true; return mapData[r][c]==1; }
    private boolean isDoor(float x, float y) { int c=(int)(x/TILE_SIZE), r=mapHeight-1-(int)(y/TILE_SIZE); if(c<0||c>=mapWidth||r<0||r>=mapHeight)return false; return mapData[r][c]==-1; }

    // Các class Helper giữ nguyên (Goblin, DemonBoss, Bullet, PathNode, Chest, DroppedItem)...
    // (Để tiết kiệm chỗ, bạn hãy paste lại các class con này từ bài trước vào đây nhé)
    // Nhớ paste đủ các class con vào cuối file!
    private Goblin findNearestEnemy() {
        Goblin nearest = null; float minDistance = DETECTION_RADIUS;
        float pX = player.x + PLAYER_SIZE/2; float pY = player.y + PLAYER_SIZE/2;
        for (Goblin enemy : enemies) {
            float dist = Vector2.dst(pX, pY, enemy.x + enemy.width/2, enemy.y + enemy.height/2);
            if (dist < minDistance) { minDistance = dist; nearest = enemy; }
        }
        return nearest;
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

    class DemonBoss extends Rectangle {
        int hp = 50, maxHp = 50;
        float hitTimer = 0;
        public DemonBoss(float x, float y, float size) { super(x, y, size, size); }
        public void takeDamage(int d) { hp -= d; hitTimer = 0.1f; }

        public void update(float dt, Rectangle player) {
            Vector2 dir = new Vector2(player.x - x, player.y - y).nor();
            float speed = 70 * dt;
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

    // Copy lại drawPauseOverlay, createPauseMenu, createVictoryMenu từ bài trước (đã có rồi)
    // Mình chỉ viết lại drawPauseOverlay ví dụ
    private void drawPauseOverlay() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(uiStage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.5f));
        shapeRenderer.rect(0, 0, 800, 480);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    private void createPauseMenu(Stage stage) {
        Table table = new Table(); table.setFillParent(true); stage.addActor(table);
        TextButton resumeButton = new TextButton("Resume", skin); TextButton quitButton = new TextButton("Quit", skin);
        table.add(resumeButton).width(200).height(50).pad(10).row(); table.add(quitButton).width(200).height(50).pad(10).row();
        resumeButton.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { resume(); } });
        quitButton.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { Gdx.app.exit(); } });
    }
    private void createVictoryMenu() {
        uiStage.clear(); Table table = new Table(); table.setFillParent(true); uiStage.addActor(table);
        com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle labelStyle = new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle(font, Color.YELLOW);
        com.badlogic.gdx.scenes.scene2d.ui.Label victoryLabel = new com.badlogic.gdx.scenes.scene2d.ui.Label("VICTORY!", labelStyle); victoryLabel.setFontScale(3);
        TextButton menuButton = new TextButton("Return to Menu", skin);
        table.add(victoryLabel).padBottom(50).row(); table.add(menuButton).width(200).height(50);
        menuButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                currentState = State.MENU; uiStage.clear(); multiplexer.removeProcessor(uiStage);
                camera.position.set(viewport.getWorldWidth()/2f, viewport.getWorldHeight()/2f, 0); camera.update();
                resetGame();
            }
        });
        multiplexer.addProcessor(uiStage);
    }

    @Override
    public void pause() {
        if (currentState == State.PLAYING) {
            currentState = State.PAUSED;
            uiStage.clear();
            createPauseMenu(uiStage);
        }
    }

    @Override
    public void resume() {
        if (currentState == State.PAUSED) {
            currentState = State.PLAYING;
            uiStage.clear();
        }
    }
}
