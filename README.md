# MCOC Arena Bot

Farm de Arena automatizado pro MCOC via Accessibility Service, sem root. Agora é um projeto Gradle completo e autossuficiente (tem gradlew, build files e manifest prontos) — não depende mais do wizard do Android Studio pra existir.

## ⚠️ Antes de tudo
Isso viola os Termos de Serviço do MCOC — a Kabam pode suspender a conta que usar automação. Use por sua conta e risco, de preferência numa conta secundária, e não em Alliance War/Battlegrounds.

## Como compilar

### 🟢 Opção A — GitHub Actions (a mais fácil sem PC)

O projeto já vem com `.github/workflows/build.yml`: a cada push, o GitHub compila o APK nos servidores dele — seu celular não faz esforço nenhum.

1. Crie uma conta grátis em github.com (se ainda não tiver) e um repositório novo (pode ser privado), ex. `mcoc-arena-bot`.
2. Suba os arquivos deste zip pro repositório mantendo a estrutura de pastas. O jeito mais confiável no celular é via **Termux** (veja Opção B) com `git`; o upload web do GitHub aceita arrastar pastas em navegador desktop, mas é chato de garantir a árvore certa pelo app mobile.
3. O push já dispara o build automaticamente — acompanhe na aba **Actions** do repositório.
4. Quando o ícone ficar verde, entre na execução → **Artifacts** → baixe `mcoc-arena-bot-debug` (um .zip com o `.apk` dentro).
5. Extraia e instale o `.apk` no celular (o Android vai pedir pra liberar "instalar apps de fontes desconhecidas" — normal fora da Play Store; pode aparecer um aviso do Play Protect também, também normal pra APK não assinado pela Play Store).

### 🟡 Opção B — Termux, mas pra Git, não pra compilar

Compilar Android *dentro* do Termux dá certo às vezes, mas é bem mais dor de cabeça do que parece: o SDK completo (platform-tools + build-tools + platform) tem mais de 1 GB pra baixar, o `aapt2` (parte do build-tools) historicamente tem binários só pra x86 — em ARM (a maioria dos celulares) precisa de um build alternativo da comunidade — e não tem Android Studio/emulador pra debugar, só terminal. É factível, mas para uma primeira compilação eu não recomendaria.

Onde o Termux realmente ajuda é como **cliente Git** pra mandar o código pro GitHub (Opção A):

```bash
pkg install git
git clone https://github.com/SEU_USUARIO/mcoc-arena-bot.git
cd mcoc-arena-bot
# copie os arquivos extraídos do zip pra dentro desta pasta
git add .
git commit -m "primeira versão"
git push
```

Isso já dispara o build na nuvem — sem precisar compilar nada localmente.

### 🔵 Opção C — Android Studio num PC (se tiver acesso, é a mais tranquila pra mexer no código)

Abra o Android Studio → **File → Open** → selecione a pasta `MCOCArenaBot` extraída (não precisa mais criar projeto novo nem copiar arquivo por arquivo — ele já reconhece a estrutura). Deixe o Gradle sincronizar e rode num dispositivo físico via USB (emuladores costumam dar problema com overlay + Accessibility Service).

## Como usar (depois de instalado)

1. Abra o app → **"1. Ativar Serviço de Acessibilidade"** → ative "MCOC Arena Bot" na lista do Android → volte.
2. Toque em **"2. Permitir sobreposição"** → conceda a permissão.
3. Abra o MCOC e entre na tela da Arena (com um campeão já selecionável).
4. Toque no ícone flutuante (⠿) → **🎯** → toque, na ordem pedida na tela, em: slot do campeão → botão Lutar → área central da luta (zona de ataque) → botão Continuar da 1ª tela pós-luta → botão Continuar da 2ª tela.
5. Toque **▶** pra iniciar. Acompanhe as primeiras 2-3 lutas antes de deixar rodando sozinho.

## Ajustes finos

No `ArenaAccessibilityService.kt`, dentro do `companion object`:

```kotlin
const val FIGHT_DURATION_TAPS = 40      // nº de toques durante o combate
const val FIGHT_LOAD_DELAY_MS = 3500L   // espera a luta carregar
const val POST_FIGHT_DELAY_MS = 1800L   // espera a animação de vitória
```

## Mantenha a tela ligada

O bot precisa da tela acesa pro jogo renderizar. **Ajustes do Android → Tela → Tempo limite** → o maior valor disponível enquanto o bot roda.

## Se o build da Opção A falhar

Ferramentas Android mudam de versão rápido — se o GitHub Actions der erro de versão (AGP/Gradle/SDK), normalmente é só um número pra ajustar em `build.gradle.kts`, `gradle/wrapper/gradle-wrapper.properties` ou no workflow. Cole o erro da aba Actions de volta aqui que eu ajusto.

## Próximos passos possíveis

Esta versão só automatiza farm/dailies (sequência de toques). Combate de verdade (Story/Alliance Quest) exigiria capturar a tela (MediaProjection) e reconhecer imagem — bem mais complexo. Posso ajudar a estender o projeto nessa direção quando quiser.
