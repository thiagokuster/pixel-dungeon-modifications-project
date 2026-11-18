# Pixel Dungeon Alterado

Roguelike de masmorras em código aberto, feito a partir do [Shattered Pixel Dungeon](https://github.com/00-Evan/shattered-pixel-dungeon), de Evan Debenham. O Shattered é baseado no [Pixel Dungeon](https://github.com/00-Evan/pixel-dungeon-gradle), de [Watabou](https://watabou.itch.io/).

Este repositório não é o jogo oficial. O nome do app é Pixel Dungeon Alterado e o ID do pacote é `com.thiagokuster.pixeldungeonalterado`, os dois definidos em `build.gradle`. Esse ID separa a instalação e os saves do Shattered Pixel Dungeon.

O código está sob a licença [GPL-3.0](LICENSE.txt). Quem modificar e distribuir também precisa publicar o código-fonte sob a mesma licença. Os créditos de Evan Debenham, Watabou e dos demais colaboradores permanecem no jogo. Os sprites de herói do Gnoll, do Xamã e do Necromante foram montados a partir das camadas do [Remixed Dungeon](https://github.com/NYRDS/remixed-dungeon), de NYRDS, também sob GPL-3.0.

## Compilar

É preciso um JDK 21. Os guias estão em `/docs`:

- [Desktop](docs/getting-started-desktop.md)
- [Android](docs/getting-started-android.md)
- [iOS](docs/getting-started-ios.md)

No desktop:

```
./gradlew desktop:debug
```

Antes de publicar na Google Play, leia o final de [docs/getting-started-android.md](docs/getting-started-android.md). A loja remove apps que parecem o jogo oficial.

## Trazer atualizações do original

Este repositório foi criado como uma cópia, não como um fork do GitHub, então um clone novo não acompanha o original sozinho. Para buscar as versões novas do Evan:

```
git remote add upstream https://github.com/00-Evan/shattered-pixel-dungeon.git
git fetch upstream
```
