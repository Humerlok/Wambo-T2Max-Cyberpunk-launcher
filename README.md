# Wambo T2Max Cyberpunk launcher

Launcher Android leve e minimalista para o projetor Wanbo T2 Max com Android 9.

## Recursos

- Interface cyberpunk com fundo otimizado em WebP.
- Fonte ProFont Nerd Font integrada.
- Atalhos para YouTube, Kodi, VLC, Play Store e navegador.
- Gaveta para todos os aplicativos instalados.
- Acesso às configurações do Android, HDMI e ajustes de imagem.
- Fechamento automático dos aplicativos gerenciados ao abrir outro.
- Limpeza segura opcional de caches.
- Indicadores de relógio, clima e Wi-Fi.
- Sem recomendações, anúncios ou carrosséis promocionais.

## Compatibilidade

- Android 9
- Arquitetura ARM32/armeabi-v7a
- Wanbo T2 Max / T2M+ com placa MTK368P/MT5862

## Compilação

Abra o projeto no Android Studio ou use Gradle:

```powershell
gradlew.bat :app:assembleDebug
```

O APK de depuração será gerado em `app/build/outputs/apk/debug/`.

## Observações

O launcher foi desenvolvido para uso pessoal neste modelo de projetor. Antes de alterar pacotes do sistema, mantenha uma forma de recuperação via ADB e confirme a função de cada pacote.
