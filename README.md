# Psula

Psula; hedefleri, görevleri ve alışkanlıkları tek bir ekranda düzenlemeye yardımcı olan, yerel öncelikli bir Android üretkenlik uygulamasıdır.

> Proje aktif geliştirme aşamasındadır. Veriler şu anda cihazda Room ile saklanır; ekrandaki senkronizasyon akışı bir arayüz prototipidir ve uzak sunucuya veri göndermez.

![Psula ekran görüntüsü](app/src/test/screenshots/greeting.png)

## Özellikler

- Hedef oluşturma, ilerleme güncelleme ve tamamlama
- Öncelik ve son tarih içeren görev yönetimi
- Günlük alışkanlık takibi ve seri hesaplama
- Hedef, görev ve alışkanlıkların birlikte gösterildiği kontrol paneli
- Açık ve koyu tema destekli Material 3 arayüzü
- Room tabanlı cihaz içi veri saklama
- JSON biçiminde yerel veri özeti oluşturma

## Teknik yapı

| Katman | Kullanılan yapı |
| --- | --- |
| Arayüz | Kotlin, Jetpack Compose, Material 3 |
| Durum yönetimi | ViewModel, StateFlow, Coroutines |
| Veri | Room, DAO, Repository |
| Test | JUnit, Robolectric, Compose UI Test, Roborazzi |
| Minimum Android | API 24 |

Uygulama, UI ile veri erişimini ayırmak için `ViewModel → Repository → DAO → Room` akışını kullanır. Room sorguları `Flow` döndürür; ViewModel bu akışları `StateFlow` olarak arayüze sunar.

## Kurulum

### Gereksinimler

- Android Studio
- JDK 17
- Android SDK 36
- Gradle 9.3.1

### Çalıştırma

1. Repoyu klonlayın:

   ```bash
   git clone https://github.com/LyrexD/Psula-Apps.git
   cd Psula-Apps
   ```

2. Projeyi Android Studio ile açın.
3. Gradle senkronizasyonunun tamamlanmasını bekleyin.
4. API 24 veya üzeri bir emülatör ya da fiziksel cihaz seçin.
5. `app` yapılandırmasını çalıştırın.

Uygulamanın mevcut özellikleri için API anahtarı gerekmez.

## Test ve kalite kontrolleri

Android Studio içinden testleri çalıştırabilir veya Gradle 9.3.1 kurulu bir ortamda aşağıdaki komutları kullanabilirsiniz:

```bash
gradle testDebugUnitTest lintDebug assembleDebug
```

GitHub Actions aynı kontrolleri her push ve pull request için çalıştırır.

## Proje yapısı

```text
app/src/main/java/com/example/
├── data/       # Room entity, DAO, database ve repository
├── ui/         # Compose ekranları ve ViewModel
└── MainActivity.kt
```

## Yol haritası

- Demo verisini tercihe bağlı hale getirmek
- Gerçek yedekleme ve geri yükleme akışı
- Bildirimler ve hatırlatıcılar
- Daha kapsamlı ViewModel ve veri katmanı testleri
- Paket adını kalıcı ürün alan adına taşımak

## Katkı

Hata bildirimi ve geliştirme önerileri için bir issue açabilirsiniz. Değişiklik göndermeden önce mevcut testlerin geçtiğinden emin olun.

## Lisans

Bu proje MIT Lisansı ile yayımlanır. Ayrıntılar için [LICENSE](LICENSE) dosyasına bakın.
