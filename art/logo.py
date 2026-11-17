"""Піксельний логотип CTE2-UK без сторонніх бібліотек: сітка клітинок -> PNG (RGBA)."""
import struct
import sys
import zlib

N = 50  # розмір сітки в клітинках

BLUE = (0, 87, 183, 255)
BLUE_LIGHT = (32, 116, 214, 255)
BLUE_DARK = (0, 62, 140, 255)
YELLOW = (255, 213, 0, 255)
YELLOW_LIGHT = (255, 228, 77, 255)
YELLOW_DARK = (222, 176, 0, 255)
OUTLINE = (24, 22, 38, 255)
WHITE = (255, 255, 255, 255)
SHADOW_BLUE = (0, 38, 92, 255)
NAVY = (12, 34, 84, 255)
SHADOW_YELLOW = (176, 132, 0, 255)
CLEAR = (0, 0, 0, 0)

GLYPHS = {
    'C': [".###.", "#...#", "#....", "#....", "#....", "#...#", ".###."],
    'T': ["#####", "..#..", "..#..", "..#..", "..#..", "..#..", "..#.."],
    'E': ["#####", "#....", "#....", "####.", "#....", "#....", "#####"],
    '2': [".###.", "#...#", "....#", "...#.", "..#..", ".#...", "#####"],
    'У': ["#...#", "#...#", "#...#", ".####", "....#", "....#", "####."],
    'К': ["#...#", "#..#.", "#.#..", "##...", "#.#..", "#..#.", "#...#"],
    'Р': ["####.", "#...#", "#...#", "####.", "#....", "#....", "#...."],
}

grid = [[CLEAR] * N for _ in range(N)]


def inside_rounded(x, y, r=4):
    """Квадрат із заокругленими кутами."""
    for cx, cy in ((r, r), (N - 1 - r, r), (r, N - 1 - r), (N - 1 - r, N - 1 - r)):
        if (x < r and y < r and cx == r and cy == r) or (x > N - 1 - r and y < r and cx == N - 1 - r and cy == r) \
                or (x < r and y > N - 1 - r and cx == r and cy == N - 1 - r) \
                or (x > N - 1 - r and y > N - 1 - r and cx == N - 1 - r and cy == N - 1 - r):
            return (x - cx) ** 2 + (y - cy) ** 2 <= r * r + r
    return True


# фон: прапор із легким об'ємом
half = N // 2
for y in range(N):
    for x in range(N):
        if not inside_rounded(x, y):
            continue
        if y < half:
            c = BLUE_LIGHT if y < 5 else (BLUE_DARK if y >= half - 2 else BLUE)
        else:
            c = YELLOW_LIGHT if y < half + 2 else (YELLOW_DARK if y >= N - 5 else YELLOW)
        grid[y][x] = c

# контур: клітинка фону, у якої є прозорий сусід або край
outline = []
for y in range(N):
    for x in range(N):
        if grid[y][x][3] == 0:
            continue
        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            nx, ny = x + dx, y + dy
            if not (0 <= nx < N and 0 <= ny < N) or grid[ny][nx][3] == 0:
                outline.append((x, y))
                break
for x, y in outline:
    grid[y][x] = OUTLINE


def text_width(text, scale, gap):
    return len(text) * 5 * scale + (len(text) - 1) * gap


def draw_text(text, top, scale, gap, color, shadow):
    left = (N - text_width(text, scale, gap)) // 2
    for layer, (ox, oy, col) in enumerate(((1, 1, shadow), (0, 0, color))):
        x0 = left
        for ch in text:
            for gy, row in enumerate(GLYPHS[ch]):
                for gx, bit in enumerate(row):
                    if bit != '#':
                        continue
                    for sy in range(scale):
                        for sx in range(scale):
                            px, py = x0 + gx * scale + sx + ox, top + gy * scale + sy + oy
                            if 0 <= px < N and 0 <= py < N and grid[py][px] != OUTLINE:
                                grid[py][px] = col
            x0 += 5 * scale + gap


draw_text('CTE2', top=5, scale=2, gap=1, color=WHITE, shadow=SHADOW_BLUE)
draw_text('УКР', top=half + 4, scale=2, gap=2, color=NAVY, shadow=SHADOW_YELLOW)


def write_png(path, cell):
    size = N * cell
    raw = bytearray()
    for y in range(size):
        raw.append(0)
        row = grid[y // cell]
        for x in range(size):
            raw.extend(row[x // cell])

    def chunk(tag, data):
        return struct.pack('>I', len(data)) + tag + data + struct.pack('>I', zlib.crc32(tag + data) & 0xffffffff)

    png = b'\x89PNG\r\n\x1a\n' + chunk(b'IHDR', struct.pack('>IIBBBBB', size, size, 8, 6, 0, 0, 0)) \
        + chunk(b'IDAT', zlib.compress(bytes(raw), 9)) + chunk(b'IEND', b'')
    with open(path, 'wb') as f:
        f.write(png)


for out, cell in zip(sys.argv[1::2], map(int, sys.argv[2::2])):
    write_png(out, cell)
    print(out, N * cell)
