# ✦ Silvera Client

**Minecraft 1.21.x | Fabric | Client-Side Cosmetic Mod**

Güzel animasyonlar, particle efektler, HUD ve animasyonlu mod menüsü.

---

## ⌨️ Tuşlar

| Tuş | Fonksiyon |
|-----|-----------|
| **RShift** | Mod menüsünü aç / kapat |
| **C** | Zoom (Zoom modülü aktifken) |

---

## 🧩 Modüller

### ✦ Particles
| Modül | Açıklama |
|-------|----------|
| Footstep Particles | Yürürken ayak izi partikülleri |
| Trail Particles | Hareket ederken iz (elytra'da özel) |
| Ambient Particles | Etrafında dönen ambient partiküller |
| Hit Particles | Vurduğunda patlayan efekt |

### ◈ HUD
| Modül | Açıklama |
|-------|----------|
| Coordinates | XYZ + Chunk göstergesi |
| FPS Counter | Renk kodlu FPS |
| Biome Display | Mevcut biome |
| Direction HUD | Yön + derece |
| Armor HUD | Zırh + durability |
| Potion HUD | Aktif efektler + süre |

### ◉ Visual
| Modül | Açıklama |
|-------|----------|
| Motion Blur | Kamera hareketinde blur |

### ❋ Cosmetic
| Modül | Açıklama |
|-------|----------|
| Elytra Trail | Elytra ile uçarken muhteşem iz |
| Cape Animation | Pelerin dalgalanması güçlendirilir |
| Hit Color | Vurulduğunda özel renk flash |
| Zoom | Smooth zoom (C tuşu) |

---

## 🔨 Build

```bash
git clone https://github.com/KullaniciAdi/SilveraClient.git
cd SilveraClient
chmod +x gradlew
./gradlew build
# JAR → build/libs/silvera-client-1.0.0.jar
```

GitHub'a push yaptığında **Actions otomatik çalışır** ve JAR artifact olarak kaydedilir.  
Release oluşturursan JAR otomatik release'e yüklenir.

---

## 📁 Dosya Yapısı

```
SilveraClient/
├── .github/workflows/build.yml
├── src/main/java/com/silvera/
│   ├── client/SilveraClient.java        ← Entrypoint
│   ├── module/
│   │   ├── api/Module.java              ← Base class
│   │   ├── api/ModuleManager.java       ← Kayıt & tick
│   │   └── impl/
│   │       ├── particle/                ← 4 particle modül
│   │       ├── hud/                     ← 6 HUD modül
│   │       ├── visual/                  ← MotionBlur
│   │       └── cosmetic/                ← ElytraTrail, Cape, HitColor, Zoom
│   ├── mixin/                           ← 5 Mixin
│   ├── render/HudRenderer.java          ← HUD koordinatörü
│   └── gui/SilveraMenuScreen.java       ← Animasyonlu menü
└── .github/workflows/build.yml
```
