package com.source.dungeonme; // <--- KIỂM TRA TÊN PACKAGE CỦA BẠN

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager; // [Báo cáo Chương 2.5] Quản lý tài nguyên
import com.badlogic.gdx.audio.Sound;         // [Báo cáo Chương 2.3.4] Module Audio
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // [Báo cáo Chương 2.3.1] Cơ chế Batching
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

// [Báo cáo Chương 2.2] Vòng đời ứng dụng (Lifecycle: create, render, dispose)
public class Main extends ApplicationAdapter {
    SpriteBatch batch;
    OrthographicCamera camera;
    BitmapFont font;
    AssetManager assetManager;

    // --- QUẢN LÝ TRẠNG THÁI GAME (STATE MACHINE) ---
    // [Báo cáo Chương 3.3] Quản lý luồng game
    enum State {
        MENU,       // Màn hình chờ
        PLAYING,    // Đang chơi
        GAME_OVER   // Kết thúc
    }
    State currentState = State.MENU; // Mặc định vào Menu

    // --- CẤU HÌNH GAME ---
    final int TILE_SIZE = 64;
    final float PLAYER_SIZE = 40;
    final float DETECTION_RADIUS = 300f;

    // Dữ liệu Map (1 = Tường, 0 = Sàn)
    int[][] mapData = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    int mapHeight = mapData.length;
    int mapWidth = mapData[0].length;

    // --- OBJECTS ---
    Rectangle player;
    Vector2 facingDir;
    float stateTime = 0f;
    float playerRotation = 0f;

    // Stats
    int playerHp = 5, maxHp = 5;
    float playerHitTimer = 0, damageCooldown = 0;

    Array<Bullet> bullets;
    Array<Goblin> enemies;

    // Audio & UI
    Sound soundShoot, soundHit;
    Texture imgBlank;

    @Override
    public void create() {
        // [Báo cáo Chương 2.3.1] SpriteBatch để tối ưu Draw Calls
        batch = new SpriteBatch();

        // [Báo cáo Chương 2.3.2] Camera Orthographic cho game 2D
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        font = new BitmapFont(); // Font mặc định

        // [Báo cáo Chương 2.5] AssetManager quản lý tài nguyên tập trung
        assetManager = new AssetManager();

        // Load Textures
        assetManager.load("knight.png", Texture.class);
        assetManager.load("floor.png", Texture.class);
        assetManager.load("bricks.png", Texture.class);
        assetManager.load("bow.png", Texture.class); // Dùng Cung
        assetManager.load("goblin faceless.png", Texture.class);
        assetManager.load("eyes.png", Texture.class);
        assetManager.load("big eyes.png", Texture.class);
        assetManager.load("evil eyes.png", Texture.class);
        assetManager.load("iron sword.png", Texture.class); // Làm mũi tên tạm

        // Load Sounds [Báo cáo Chương 2.3.4]
        try {
            assetManager.load("bowShoot.wav", Sound.class);
            assetManager.load("goblinHitHurt.wav", Sound.class);
        } catch (Exception e) { Gdx.app.log("Warn", "Missing sound files"); }

        // Chờ tải xong (Synchronous)
        assetManager.finishLoading();

        // Lấy Sound an toàn
        if (assetManager.isLoaded("shoot.wav")) soundShoot = assetManager.get("shoot.wav", Sound.class);
        if (assetManager.isLoaded("hit.wav")) soundHit = assetManager.get("hit.wav", Sound.class);

        // Tạo texture trắng để vẽ thanh máu
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE); pixmap.fill();
        imgBlank = new Texture(pixmap); pixmap.dispose();

        // Khởi tạo game ban đầu
        resetGame();
    }

    // Hàm reset game (Dùng khi mới vào hoặc chơi lại)
    private void resetGame() {
        player = new Rectangle(150, 150, PLAYER_SIZE, PLAYER_SIZE);
        facingDir = new Vector2(1, 0);
        playerHp = 5;
        bullets = new Array<>();
        enemies = new Array<>();
        for(int i=0; i<5; i++) spawnEnemy();
    }

    private void spawnEnemy() {
        float x, y;
        int safetyLoop = 0;
        do {
            x = MathUtils.random(TILE_SIZE, (mapWidth - 1) * TILE_SIZE);
            y = MathUtils.random(TILE_SIZE, (mapHeight - 1) * TILE_SIZE);
            safetyLoop++;
        } while (isWall(x, y, 40) && safetyLoop < 100);

        String[] eyes = {"eyes.png", "big eyes.png", "evil eyes.png"};
        Texture randomEye = assetManager.get(eyes[MathUtils.random(0, 2)], Texture.class);
        enemies.add(new Goblin(x, y, 40, randomEye));
    }

    @Override
    public void render() {
        // Clear Screen [Báo cáo Chương 2.2]
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();

        // --- STATE MACHINE (QUẢN LÝ TRẠNG THÁI) ---
        switch (currentState) {
            case MENU:
                updateMenu();
                break;
            case PLAYING:
                updateGameLogic(deltaTime);
                drawGame();
                if (playerHp <= 0) currentState = State.GAME_OVER;
                break;
            case GAME_OVER:
                updateGameOver();
                break;
        }
    }

    // --- MÀN HÌNH MENU ---
    private void updateMenu() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            currentState = State.PLAYING;
        }

        batch.begin();
        font.getData().setScale(2);
        font.setColor(Color.YELLOW);
        font.draw(batch, "DUNGEON SURVIVOR", 250, 300);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        if ((System.currentTimeMillis() / 500) % 2 == 0) { // Nhấp nháy
            font.draw(batch, "Press ENTER to Start", 280, 200);
        }
        batch.end();
    }

    // --- MÀN HÌNH GAME OVER ---
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

    // --- LOGIC GAMEPLAY (UPDATE) ---
    private void updateGameLogic(float deltaTime) {
        stateTime += deltaTime;

        // 1. INPUT POLLING [Báo cáo Chương 2.3.3]
        float speed = 200 * deltaTime;
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

        // 2. AUTO-AIM & CAMERA
        Goblin target = findNearestEnemy();
        if (target != null) {
            float pCenterX = player.x + PLAYER_SIZE/2;
            float pCenterY = player.y + PLAYER_SIZE/2;
            facingDir.set((target.x + target.width/2) - pCenterX, (target.y + target.height/2) - pCenterY).nor();
        } else if (isMoving) {
            facingDir.set(inputX, inputY).nor();
        }

        // Camera Lerp
        float targetCamX = player.x + PLAYER_SIZE / 2;
        float targetCamY = player.y + PLAYER_SIZE / 2;
        if (target != null) {
            targetCamX += ((target.x + target.width/2) - targetCamX) * 0.3f;
            targetCamY += ((target.y + target.height/2) - targetCamY) * 0.3f;
        }
        camera.position.x += (targetCamX - camera.position.x) * 0.1f;
        camera.position.y += (targetCamY - camera.position.y) * 0.1f;
        camera.update();

        // 3. SHOOTING
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float handOffset = 16f;
            float bulletStartX = (facingDir.x >= 0) ? (player.x + PLAYER_SIZE/2 + handOffset) : (player.x + PLAYER_SIZE/2 - handOffset);
            float bulletStartY = player.y + PLAYER_SIZE/2 - 8f;
            bullets.add(new Bullet(bulletStartX, bulletStartY, facingDir.angleDeg()));
            if (soundShoot != null) soundShoot.play(0.5f);
        }
    }

    // --- VẼ GAMEPLAY (DRAW) ---
    private void drawGame() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // A. MAP
        Texture imgFloor = assetManager.get("floor.png", Texture.class);
        Texture imgWall = assetManager.get("bricks.png", Texture.class);
        for (int row = 0; row < mapHeight; row++) {
            for (int col = 0; col < mapWidth; col++) {
                float x = col * TILE_SIZE;
                float y = (mapHeight - 1 - row) * TILE_SIZE;
                batch.draw(imgFloor, x, y, TILE_SIZE, TILE_SIZE);
                if (mapData[row][col] == 1) batch.draw(imgWall, x, y, TILE_SIZE, TILE_SIZE);
            }
        }

        // B. QUÁI (AI BFS + SLIDING)
        if (damageCooldown > 0) damageCooldown -= Gdx.graphics.getDeltaTime();
        if (playerHitTimer > 0) playerHitTimer -= Gdx.graphics.getDeltaTime();

        Texture imgGoblinBody = assetManager.get("goblin faceless.png", Texture.class);

        for(Goblin enemy : enemies) {
            // Logic BFS
            enemy.pathTimer += Gdx.graphics.getDeltaTime();
            if (enemy.pathTimer > 0.2f) {
                enemy.pathTimer = 0;
                enemy.nextStep = findNextStep(enemy.x + 20, enemy.y + 20, player.x + 20, player.y + 20);
            }
            Vector2 dir;
            if (enemy.nextStep != null) {
                dir = new Vector2(enemy.nextStep.x - (enemy.x + 20), enemy.nextStep.y - (enemy.y + 20)).nor();
                if(Vector2.dst(enemy.x, enemy.y, enemy.nextStep.x - 20, enemy.nextStep.y - 20) < 5) dir.setZero();
            } else {
                dir = new Vector2(player.x - enemy.x, player.y - enemy.y).nor();
            }

            // Logic Di chuyển (Sliding)
            float enemySpeed = 100 * Gdx.graphics.getDeltaTime();
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

            // Va chạm Player (Cắn)
            Rectangle enemyHitbox = new Rectangle(enemy.x + 10, enemy.y + 10, 20, 20);
            if (player.overlaps(enemyHitbox) && damageCooldown <= 0) {
                playerHp--; damageCooldown = 1.0f; playerHitTimer = 0.2f;
                Vector2 pushDir = new Vector2(player.x - enemy.x, player.y - enemy.y).nor();
                player.x += pushDir.x * 50; player.y += pushDir.y * 50;
            }

            // Vẽ Quái
            if (enemy.hitTimer > 0) { enemy.hitTimer -= Gdx.graphics.getDeltaTime(); batch.setColor(1, 0, 0, 1); }
            else batch.setColor(1, 1, 1, 1);
            batch.draw(imgGoblinBody, enemy.x, enemy.y, 40, 40);
            batch.draw(enemy.eyeTexture, enemy.x, enemy.y, 40, 40);
            batch.setColor(1, 1, 1, 1);
        }

        // C. PLAYER
        if (playerHitTimer > 0) batch.setColor(1, 0, 0, 1);
        else if (damageCooldown > 0) {
            if (MathUtils.sin(stateTime * 20) > 0) batch.setColor(1, 1, 1, 0.5f);
            else batch.setColor(1, 1, 1, 1);
        } else batch.setColor(1, 1, 1, 1);

        Texture imgPlayer = assetManager.get("knight.png", Texture.class);
        TextureRegion playerFrame = new TextureRegion(imgPlayer);
        if (facingDir.x < 0 && !playerFrame.isFlipX()) playerFrame.flip(true, false);
        if (facingDir.x > 0 && playerFrame.isFlipX()) playerFrame.flip(true, false);

        batch.draw(playerFrame, player.x, player.y, PLAYER_SIZE/2, PLAYER_SIZE/2, PLAYER_SIZE, PLAYER_SIZE, 1, 1 + MathUtils.sin(stateTime*5)*0.05f, playerRotation);
        batch.setColor(1, 1, 1, 1);

        // D. WEAPON (BOW)
        float handOffsetX = 16f; float handOffsetY = -8f; float baseRotation = -45f;
        float weaponX, weaponY, weaponRotation;
        boolean flipX;
        if (facingDir.x >= 0) {
            weaponX = (player.x + PLAYER_SIZE/2) + handOffsetX; weaponY = (player.y + PLAYER_SIZE/2) + handOffsetY;
            weaponRotation = baseRotation; flipX = false;
        } else {
            weaponX = (player.x + PLAYER_SIZE/2) - handOffsetX; weaponY = (player.y + PLAYER_SIZE/2) + handOffsetY;
            weaponRotation = -baseRotation; flipX = true;
        }
        Texture imgWeapon = assetManager.get("bow.png", Texture.class);
        TextureRegion wRegion = new TextureRegion(imgWeapon);
        if (flipX) wRegion.flip(true, false);
        batch.draw(wRegion, weaponX - 16, weaponY - 16, 16, 16, 32, 32, 1, 1, weaponRotation);

        // E. BULLETS
        Iterator<Bullet> iter = bullets.iterator();
        while(iter.hasNext()) {
            Bullet b = iter.next();
            b.update(Gdx.graphics.getDeltaTime());
            if (isCellBlocked(b.x, b.y)) { iter.remove(); continue; }

            Texture imgArrow = assetManager.get("iron sword.png", Texture.class); // Mũi tên tạm
            batch.draw(new TextureRegion(imgArrow), b.x-10, b.y-10, 10, 10, 20, 20, 1, 1, b.angle-45);

            Rectangle bRect = new Rectangle(b.x-10, b.y-10, 20, 20);
            boolean hit = false;
            Iterator<Goblin> eIter = enemies.iterator();
            while(eIter.hasNext()) {
                Goblin enemy = eIter.next();
                if(bRect.overlaps(enemy)) {
                    enemy.takeDamage(1);
                    if (enemy.hp <= 0) {
                        eIter.remove();
                        if (soundHit != null) soundHit.play(0.5f);
                    }
                    hit = true; break;
                }
            }
            if(hit || b.lifeTime > 2) iter.remove();
        }

        // F. UI (HP BAR)
        float hpBarWidth = 50; float hpBarHeight = 6;
        float hpBarX = player.x + (PLAYER_SIZE - hpBarWidth) / 2;
        float hpBarY = player.y + PLAYER_SIZE + 10;
        batch.setColor(1, 0, 0, 1);
        batch.draw(imgBlank, hpBarX, hpBarY, hpBarWidth, hpBarHeight);
        float currentHpWidth = hpBarWidth * ((float)playerHp / maxHp);
        if (currentHpWidth > 0) {
            batch.setColor(0, 1, 0, 1);
            batch.draw(imgBlank, hpBarX, hpBarY, currentHpWidth, hpBarHeight);
        }
        batch.setColor(1, 1, 1, 1);

        batch.end();
    }

    // [Báo cáo Chương 2.5] Giải phóng bộ nhớ
    @Override
    public void dispose() {
        batch.dispose();
        imgBlank.dispose();
        assetManager.dispose();
    }

    // --- HELPER CLASSES & METHODS ---

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

    // --- BFS ALGORITHM [Báo cáo Chương 3] ---
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
    class Bullet {
        float x, y, vx, vy, angle, lifeTime;
        public Bullet(float x, float y, float angle) {
            this.x = x; this.y = y; this.angle = angle;
            this.vx = MathUtils.cosDeg(angle) * 600; this.vy = MathUtils.sinDeg(angle) * 600;
        }
        public void update(float dt) { x += vx * dt; y += vy * dt; lifeTime += dt; }
    }
    class PathNode { int x, y; PathNode parent; public PathNode(int x, int y, PathNode p) { this.x=x; this.y=y; this.parent=p; }}
}
