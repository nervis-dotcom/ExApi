# 📜 Changelog

Todas las versiones y cambios de la API `NervisAPI`.

## [2.0.0] - 2025-07-05
### Añadido
- Soporte para `Locale` en formateo de tiempo.
- Nuevos tipos de formato: `VERBOSE`, `COMPACT`, `MINECRAFT_STYLE`.

### Deprecado
- `formatTime(long)` ahora está deprecated. Usar versión con `Locale`.

## [1.5.0] - 2025-06-15
### Añadido
- `TimeFormatType` enum.
- Formato `SHORT_TEXTUAL`.

## [1.0.0] - 2025-06-01
### Inicial
- Método básico `formatTime(long)`.
