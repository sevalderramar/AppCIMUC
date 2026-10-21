import java.util.*;
import com.example.appcimuc.model.Proyecto;
import com.example.appcimuc.repository.AppRepository;
import com.example.appcimuc.viewmodel.*;

// Ejecuta funciones reales compiladas, sin Android ni framework adicional.
// No verifica callbacks UI, propietarios ViewModel, scroll ni rotación.
class MvpChecks {
    static int checks;
    static final AppRepository repo = new AppRepository();
    static void check(boolean value, String label) {
        if (!value) throw new AssertionError(label);
        checks++;
    }
    static ExplorarUiState query(TipoContenido tipo, String texto, String categoria, Integer anio,
                                String capacidades, String integrantes) {
        return new ExplorarUiState(tipo, texto, categoria, anio, capacidades, integrantes,
            repo.getCategorias(), List.of(2026, 2025), List.of(), List.of(), List.of(), 0, 0, 0);
    }
    static ExplorarUiState filter(ExplorarUiState s) {
        return ExplorarUiStateKt.filtrarExplorar(s, repo.getProyectos(), repo.getCapacidades(), repo.getIntegrantes());
    }
    static ContactoUiState contact(Proyecto p, String nombre, String correo, String mensaje, PasoContacto paso) {
        return new ContactoUiState(p, nombre, correo, mensaje, new ErroresContacto(null, null, null), paso);
    }
    static void exploration() {
        var tipo = TipoContenido.PROYECTOS;
        var all = filter(query(tipo, "", null, null, "", ""));
        check(all.getCantidadResultados() == 3 && !all.getCatalogoVacio(), "tres proyectos");
        check(all.getTotalCapacidades() == 2 && all.getTotalIntegrantes() == 2, "totales de origen");
        check(!all.getTieneBusquedaOFiltros(), "sin restricciones");
        var title = filter(query(tipo, "EFICIENCIA", null, null, "", ""));
        check(title.getProyectos().size() == 1 && title.getProyectos().get(0).getId().equals("proy-energia"), "título y mayúsculas");
        var spaces = filter(query(tipo, "  MATERIALES    RECICLADOS  ", null, null, "", ""));
        check(spaces.getCantidadResultados() == 1 && spaces.getProyectos().get(0).getId().equals("proy-reciclaje"), "normaliza espacios");
        var description = filter(query(tipo, "mezclas recicladas", null, null, "", ""));
        check(description.getCantidadResultados() == 1, "busca descripción");
        check(filter(query(tipo, "", "cat-materiales", null, "", "")).getCantidadResultados() == 2, "categoría");
        check(filter(query(tipo, "", null, 2026, "", "")).getCantidadResultados() == 2, "año");
        var combined = filter(query(tipo, "ENSAYOS", "cat-materiales", 2026, "", ""));
        check(combined.getCantidadResultados() == 1 && combined.getProyectos().get(0).getId().equals("proy-mezclas"), "AND búsqueda/categoría/año");
        check(combined.getTieneBusquedaOFiltros(), "acción limpiar habilitable");
        check(filter(query(tipo, "ENSAYOS", "cat-materiales", 2025, "", "")).getCantidadResultados() == 0, "AND excluyente");
        var noMatch = filter(query(tipo, "zzzinexistente", null, null, "", ""));
        check(noMatch.getCantidadResultados() == 0 && !noMatch.getCatalogoVacio(), "sin coincidencias no es catálogo vacío");
        check(noMatch.getTotalProyectos() == 3 && noMatch.getTieneBusquedaOFiltros(), "origen y limpieza disponibles");
        var empty = ExplorarUiStateKt.filtrarExplorar(query(tipo, "", null, null, "", ""), List.of(), List.of(), List.of());
        check(empty.getCatalogoVacio() && empty.getCantidadResultados() == 0, "catálogo vacío");
        check(filter(query(tipo, " \t\n ", null, null, "", "")).getCantidadResultados() == 3, "consulta de espacios");
        // Valores de limpieza: no equivale a ejecutar el callback limpiar del ViewModel.
        check(filter(query(tipo, "", null, null, "", "")).getProyectos().equals(all.getProyectos()), "valores de limpieza recuperan catálogo");
        var caps = filter(query(TipoContenido.CAPACIDADES, "", null, null, "MATERIALES", ""));
        check(caps.getCantidadResultados() == 1 && caps.getCapacidades().get(0).getId().equals("cap-ensayos"), "nombre de capacidad");
        check(filter(query(TipoContenido.CAPACIDADES, "", null, null, "comparar escenarios", "")).getCantidadResultados() == 1, "descripción de capacidad");
        var members = filter(query(TipoContenido.INTEGRANTES, "", null, null, "", "ANA"));
        check(members.getCantidadResultados() == 1 && members.getIntegrantes().get(0).getId().equals("int-ana"), "nombre de integrante");
        check(filter(query(TipoContenido.INTEGRANTES, "", null, null, "", "PROCESOS")).getCantidadResultados() == 1, "descripción de integrante");
        var independent = filter(query(TipoContenido.CAPACIDADES, "sin coincidencias", "cat-materiales", 2025, "MODELADO", "ANA"));
        check(independent.getCantidadResultados() == 1 && independent.getProyectos().isEmpty()
            && independent.getIntegrantes().size() == 1, "consultas independientes y filtros solo de proyectos");
        check(independent.getTexto().equals("MODELADO") && independent.getTextoProyectos().equals("sin coincidencias")
            && independent.getTextoIntegrantes().equals("ANA"), "conserva las tres consultas");
        check(filter(independent).equals(independent), "filtrar es idempotente");
        check(repo.getIntegrante("inexistente") == null && repo.getCapacidad("inexistente") == null, "IDs relacionados inexistentes");
        check(repo.getCategoria("inexistente") == null && repo.getProyectosDeIntegrante("inexistente").isEmpty()
            && repo.getProyectosDeCapacidad("inexistente").isEmpty(), "relaciones inversas inexistentes");
    }
    static void contactFlow() {
        var p = repo.getProyecto("proy-energia");
        var blank = ContactoUiStateKt.validarContacto("", "", "");
        check(blank.getNombre() != null && blank.getCorreo() != null && blank.getMensaje() != null, "tres obligatorios");
        check(!ContactoUiStateKt.validarContacto(" \t ", " \n ", " \t\n ").getEsValido(), "rechaza espacios");
        check(ContactoUiStateKt.validarContacto("Persona Ejemplo", "demo+alias@sub.example.test", "Mensaje ficticio").getEsValido(), "correo no UC con alias");
        check(ContactoUiStateKt.validarContacto("Persona Ejemplo", "  demo@example.test  ", "Mensaje ficticio").getEsValido(), "correo con espacios exteriores");
        for (String email : List.of("invalido", "demo@", "@example.test", "demo@localhost", "demo prueba@example.test",
                "demo@exam ple.test", "demo..prueba@example.test", ".demo@example.test", "demo.@example.test",
                "demo@-example.test", "demo@example-.test", "demo@example..test", "demo@example_test.test")) {
            check(ContactoUiStateKt.validarContacto("Persona Ejemplo", email, "Mensaje ficticio").getCorreo() != null, "formato inválido");
        }
        check(ContactoUiStateKt.validarContacto("Persona Ejemplo", "a".repeat(65) + "@example.test", "Mensaje").getCorreo() != null, "límite local del correo");
        var draft = contact(p, "Persona Ejemplo", "invalido", "Texto conservado", PasoContacto.FORMULARIO);
        var rejected = ContactoUiStateKt.revisarContacto(draft);
        check(rejected.getPaso() == PasoContacto.FORMULARIO && rejected.getErrores().getCorreo() != null, "no avanza inválido");
        check(rejected.getNombre().equals(draft.getNombre()) && rejected.getCorreo().equals(draft.getCorreo())
            && rejected.getMensaje().equals(draft.getMensaje()), "conserva valores con error");
        var valid = contact(p, " Persona Ejemplo ", " demo@example.test ", " Línea uno\nLínea dos ", PasoContacto.FORMULARIO);
        var summary = ContactoUiStateKt.revisarContacto(valid);
        check(summary.getPaso() == PasoContacto.RESUMEN && summary.getErrores().getEsValido(), "resumen válido");
        check(summary.getNombre().equals("Persona Ejemplo") && summary.getCorreo().equals("demo@example.test")
            && summary.getMensaje().equals("Línea uno\nLínea dos"), "trim sin perder líneas interiores");
        var edited = ContactoUiStateKt.editarContacto(summary);
        check(edited.getPaso() == PasoContacto.FORMULARIO && edited.getNombre().equals(summary.getNombre())
            && edited.getCorreo().equals(summary.getCorreo()) && edited.getMensaje().equals(summary.getMensaje()), "editar conserva datos");
        check(ContactoUiStateKt.confirmarContacto(edited) == edited, "no salta revisión");
        check(ContactoUiStateKt.revisarContacto(summary) == summary, "revisión duplicada sin efecto");
        var done = ContactoUiStateKt.confirmarContacto(summary);
        check(done.getPaso() == PasoContacto.CONFIRMACION, "confirmación local");
        check(done.getNombre().isEmpty() && done.getCorreo().isEmpty() && done.getMensaje().isEmpty(), "borra datos tras confirmar");
        check(done.getProyecto() == p && p.getCuposDisponibles() == 2 && p.getIntegranteIds().size() == 1, "proyecto, cupos y equipo inalterados");
        check(ContactoUiStateKt.confirmarContacto(done) == done, "confirmación idempotente");
        check(ContactoUiStateKt.editarContacto(done) == done && ContactoUiStateKt.revisarContacto(done) == done, "no reabre resultado");
        var invalidSummary = contact(p, "Persona Ejemplo", "invalido", "Mensaje", PasoContacto.RESUMEN);
        var safe = ContactoUiStateKt.confirmarContacto(invalidSummary);
        check(safe.getPaso() == PasoContacto.FORMULARIO && safe.getErrores().getCorreo() != null, "resumen inválido vuelve al formulario");
        var missing = contact(null, "Persona Ejemplo", "demo@example.test", "Mensaje", PasoContacto.FORMULARIO);
        check(ContactoUiStateKt.revisarContacto(missing) == missing, "ID inexistente sin avance");
        var missingSummary = contact(null, "Persona Ejemplo", "demo@example.test", "Mensaje", PasoContacto.RESUMEN);
        check(ContactoUiStateKt.confirmarContacto(missingSummary).getPaso() == PasoContacto.FORMULARIO, "ID inexistente no confirma");
    }
    public static void main(String[] args) {
        exploration();
        contactFlow();
        System.out.println(checks + " comprobaciones JVM de exploración/contacto correctas; no prueban UI ni lifecycle.");
    }
}
