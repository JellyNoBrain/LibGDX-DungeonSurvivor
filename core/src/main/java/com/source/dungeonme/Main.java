package com.source.dungeonme; // Đảm bảo dòng này đúng với package trong máy bạn

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;

public class Main extends ApplicationAdapter {
    SpriteBatch batch;
    OrthographicCamera camera;

    Texture imgPlayer;
    Texture imgEnemy;
    Texture imgFloor;
    Texture imgBullet;

    Rectangle player;
    Array<Rectangle> enemies;
    Array<Vector2> bullets;
    Array<Vector2> bulletDirs;

    long lastEnemyTime;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // --- CẬP NHẬT TÊN ẢNH KHỚP VỚI FOLDER CỦA BẠN ---
        imgPlayer = new Texture("knight.png");       // Hiệp sĩ
        imgEnemy = new Texture("goblin.png");        // Quái vật Goblin
        imgFloor = new Texture("floor.png");         // Nền nhà
        imgBullet = new Texture("iron sword.png");   // Đạn là cây kiếm

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Khởi tạo Player
        player = new Rectangle();
        player.x = 800 / 2 - 32;
        player.y = 480 / 2 - 32;
        player.width = 64; // Nếu ảnh 32px thì nó sẽ tự phóng to lên gấp đôi
        player.height = 64;

        enemies = new Array<>();
        bullets = new Array<>();
        bulletDirs = new Array<>();

        spawnEnemy();
    }

    private void spawnEnemy() {
        Rectangle enemy = new Rectangle();
        // Sinh quái ngẫu nhiên xa hơn màn hình
        enemy.x = MathUtils.random(0, 1600);
        enemy.y = MathUtils.random(0, 1200);
        enemy.width = 64;
        enemy.height = 64;
        enemies.add(enemy);
        lastEnemyTime = TimeUtils.nanoTime();
    }

    @Override
    public void render() {
        // Màu nền tối (Dungeon)
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // --- UPDATE LOGIC ---
        float deltaTime = Gdx.graphics.getDeltaTime();

        // 1. Di chuyển Player (WASD)
        float speed = 200 * deltaTime;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) player.y += speed;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) player.y -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) player.x -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) player.x += speed;

        // 2. Camera bám theo Player (Lerp cho mượt)
        camera.position.x += (player.x - camera.position.x) * 0.1f;
        camera.position.y += (player.y - camera.position.y) * 0.1f;
        camera.update();

        // 3. Bắn đạn (Click chuột trái)
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos); // Chuyển tọa độ chuột

            Vector2 bulletPos = new Vector2(player.x + 32, player.y + 32);
            // Tính hướng bay từ nhân vật đến con trỏ chuột
            Vector2 direction = new Vector2(touchPos.x - bulletPos.x, touchPos.y - bulletPos.y).nor();

            bullets.add(bulletPos);
            bulletDirs.add(direction);
        }

        // --- DRAW ---
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Vẽ nền (Lặp lại để map rộng)
        for (int x = 0; x < 2000; x += 64) {
            for (int y = 0; y < 2000; y += 64) {
                batch.draw(imgFloor, x, y);
            }
        }

        // Vẽ Player
        batch.draw(imgPlayer, player.x, player.y);

        // Vẽ & Update Quái Goblin
        if (TimeUtils.nanoTime() - lastEnemyTime > 1000000000) spawnEnemy(); // 1 giây 1 con

        Iterator<Rectangle> iter = enemies.iterator();
        while (iter.hasNext()) {
            Rectangle enemy = iter.next();

            // AI: Quái tự đuổi theo Player
            Vector2 direction = new Vector2(player.x - enemy.x, player.y - enemy.y).nor();
            enemy.x += direction.x * 100 * deltaTime;
            enemy.y += direction.y * 100 * deltaTime;

            batch.draw(imgEnemy, enemy.x, enemy.y);

            // Xử lý va chạm với Player
            if (enemy.overlaps(player)) {
                // Tạm thời reset vị trí nếu bị bắt (Game Over)
                player.x = 800/2; player.y = 480/2;
            }
        }

        // Vẽ & Update Đạn Kiếm
        for (int i = 0; i < bullets.size; i++) {
            Vector2 b = bullets.get(i);
            Vector2 d = bulletDirs.get(i);
            b.x += d.x * 500 * deltaTime; // Kiếm bay nhanh
            b.y += d.y * 500 * deltaTime;

            // Có thể chỉnh xoay kiếm theo hướng bay ở đây nếu muốn nâng cao
            batch.draw(imgBullet, b.x, b.y);

            // Xử lý đạn trúng quái
            Rectangle bulletRect = new Rectangle(b.x, b.y, 32, 32);
            Iterator<Rectangle> enemyIter = enemies.iterator();
            while(enemyIter.hasNext()){
                Rectangle enemy = enemyIter.next();
                if(bulletRect.overlaps(enemy)){
                    enemyIter.remove(); // Xóa quái
                    // Đạn bay xuyên táo luôn cho mạnh :D
                    break;
                }
            }
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        imgPlayer.dispose();
        imgEnemy.dispose();
        imgFloor.dispose();
        imgBullet.dispose();
    }
}
