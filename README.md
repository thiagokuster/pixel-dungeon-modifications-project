# Pixel Dungeon Alterado

Roguelike de masmorras em código aberto, baseado no [Shattered Pixel Dungeon](https://github.com/00-Evan/shattered-pixel-dungeon), de Evan Debenham. O Shattered, por sua vez, é baseado no [Pixel Dungeon](https://github.com/00-Evan/pixel-dungeon-gradle), de [Watabou](https://watabou.itch.io/).

Este repositório é uma cópia independente. Não é o jogo oficial e não recebe as atualizações dele automaticamente.

O código está sob a licença [GPL-3.0](LICENSE.txt). Versões modificadas também precisam permanecer em código aberto, com a mesma licença. Os créditos de quem já contribuiu permanecem no jogo.

## Compilar

Os guias estão em `/docs`:

- [Desktop](docs/getting-started-desktop.md)
- [Android](docs/getting-started-android.md)
- [iOS](docs/getting-started-ios.md)

No desktop, com um JDK 21:

```
./gradlew desktop:debug
```

## Atualizar a partir do original

O remoto `upstream` aponta para o repositório oficial:

```
git fetch upstream
```
