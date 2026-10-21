import java.util.*;
import java.util.zip.ZipFile;
import java.nio.charset.StandardCharsets;
import com.example.appcimuc.model.Proyecto;
import com.example.appcimuc.repository.AppRepository;
import com.example.appcimuc.viewmodel.*;

// Harness JVM sin framework: prueba clases reales compiladas, no Android/Compose.
// Solo datos ficticios. Ejecutar después de assembleDebug; comando en README.
class Historia2Checks {
    static int checks;
    static final AppRepository repo = new AppRepository();
    static void check(boolean value, String label) {
        if (!value) throw new AssertionError(label);
        checks++;
    }
    static ParticipacionUiState initial(Proyecto p) {
        return new ParticipacionUiState(p, ModalParticipacion.CERRADO,
            new FormularioParticipacion(Map.of(), Map.of(), 0), new FormularioParticipacion(Map.of(), Map.of(), 0));
    }
    static FormularioParticipacion form(ParticipacionUiState s, TipoParticipacion t) {
        return t == TipoParticipacion.POSTULACION ? s.getPostulacion() : s.getAporte();
    }
    static ParticipacionUiState change(ParticipacionUiState s, TipoParticipacion t, CampoParticipacion c, String v) {
        return ParticipacionUiStateKt.cambiarCampoParticipacion(s, t, c, v);
    }
    static ParticipacionUiState fill(ParticipacionUiState s, TipoParticipacion t, String email, String source) {
        for (var c : ParticipacionUiStateKt.camposParticipacion(t)) {
            s = change(s, t, c, switch (c) {
                case CORREO -> email;
                case FUENTE -> source;
                default -> "Dato de demostración";
            });
        }
        return s;
    }
    static void relations(String id, Set<String> members, Set<String> skills) {
        var p = repo.getProyecto(id);
        check(p != null && !p.getTitulo().isBlank() && !p.getDescripcion().isBlank()
            && !p.getEstado().isBlank(), "metadatos propios: " + id);
        var team = repo.getIntegrantes(id);
        var caps = repo.getCapacidades(id);
        check(new HashSet<>(p.getIntegranteIds()).equals(members), "equipo por ID");
        check(new HashSet<>(p.getCapacidadIds()).equals(skills), "capacidades por ID");
        check(team.size() == members.size() && caps.size() == skills.size(), "relaciones resueltas y contador");
        check(team.stream().allMatch(it -> members.contains(it.getId())
            && repo.getProyectosDeIntegrante(it.getId()).contains(p)), "enlaces inversos de integrantes");
        check(caps.stream().allMatch(it -> skills.contains(it.getId())
            && repo.getProyectosDeCapacidad(it.getId()).contains(p)), "enlaces inversos de capacidades");
    }
    public static void main(String[] args) throws Exception {
        relations("proy-reciclaje", Set.of("int-ana", "int-luis"), Set.of("cap-ensayos"));
        relations("proy-energia", Set.of("int-luis"), Set.of("cap-modelado"));
        relations("proy-mezclas", Set.of("int-ana"), Set.of("cap-ensayos", "cap-modelado"));
        check(repo.getProyecto("inexistente") == null, "ID inexistente");
        check(repo.getIntegrantes("inexistente").isEmpty() && repo.getCapacidades("inexistente").isEmpty()
            && repo.getRecursos("inexistente").isEmpty(), "sin relaciones ajenas");
        check(repo.getRecursos("proy-mezclas").isEmpty(), "recursos vacíos");
        check(repo.getIntegrantes().stream().allMatch(it -> it.getCargo() == null && it.getEspecialidad() == null),
            "no se rellenan cargos ni especialidades ausentes");
        var p = repo.getProyecto("proy-energia");
        check(p.getPuedePostular(), "con cupos y acepta");
        check(!repo.getProyecto("proy-reciclaje").getPuedePostular(), "cerrado sin cupos");
        for (Integer cupos : new Integer[]{null, 0, 2}) for (Boolean acepta : new Boolean[]{null, false, true}) {
            var variante = new Proyecto(p.getId(), p.getTitulo(), p.getDescripcion(), p.getCategoriaId(), p.getAnio(),
                p.getIntegranteIds(), p.getCapacidadIds(), p.getEstado(), p.getImagenRes(), cupos, acepta);
            boolean elegible = Boolean.TRUE.equals(acepta) && cupos != null && cupos > 0;
            check(variante.getPuedePostular() == elegible, "matriz de disponibilidad, sin confundir null con cero");
            check((ParticipacionUiStateKt.abrirParticipacion(initial(variante), TipoParticipacion.POSTULACION).getModal()
                == ModalParticipacion.POSTULACION) == elegible, "apertura según elegibilidad");
        }
        for (var t : TipoParticipacion.values()) {
            var abierto = ParticipacionUiStateKt.abrirParticipacion(initial(p), t);
            check(ParticipacionUiStateKt.abrirParticipacion(abierto, t) == abierto, "no duplica apertura");
            var vacio = ParticipacionUiStateKt.enviarParticipacion(abierto, t);
            check(form(vacio, t).getErrores().size() == 4, "cuatro obligatorios");
            check(form(vacio, t).getErrores().keySet().iterator().next() == CampoParticipacion.NOMBRE,
                "primer campo incorrecto en orden");
            for (var c : ParticipacionUiStateKt.camposParticipacion(t)) vacio = change(vacio, t, c, " \t\n ");
            var espacios = ParticipacionUiStateKt.enviarParticipacion(vacio, t);
            check(form(espacios, t).getErrores().size() == 4 && form(espacios, t).getIntento() == 2,
                "espacios y nuevo intento");
            var invalido = fill(espacios, t, "invalido", "Referencia bibliográfica, 2026");
            var rechazado = ParticipacionUiStateKt.enviarParticipacion(invalido, t);
            check(form(rechazado, t).getErrores().size() == 1
                && form(rechazado, t).getErrores().containsKey(CampoParticipacion.CORREO), "correo inválido");
            check(form(rechazado, t).getValores().equals(form(invalido, t).getValores()), "conserva valores al fallar");
            var correcto = change(rechazado, t, CampoParticipacion.CORREO, "persona+demo@example.test");
            check(form(correcto, t).getErrores().isEmpty(), "correo no UC y limpia error");
            var exito = ParticipacionUiStateKt.enviarParticipacion(correcto, t);
            check(exito.getModal() == (t == TipoParticipacion.POSTULACION
                ? ModalParticipacion.EXITO_POSTULACION : ModalParticipacion.EXITO_APORTE), "confirmaciones separadas");
            check(form(exito, t).getValores().isEmpty(), "elimina datos al confirmar");
            check(ParticipacionUiStateKt.enviarParticipacion(exito, t) == exito, "envío duplicado sin efecto");
            check(exito.getProyecto() == p && p.getCuposDisponibles() == 2 && p.getIntegranteIds().size() == 1,
                "asociado al proyecto, cupos/equipo inalterados");
            var cerrado = ParticipacionUiStateKt.cerrarParticipacion(exito);
            check(cerrado.getModal() == ModalParticipacion.CERRADO
                && ParticipacionUiStateKt.enviarParticipacion(cerrado, t) == cerrado, "resultado no reaparece");
            var reabierto = ParticipacionUiStateKt.abrirParticipacion(cerrado, t);
            check(form(reabierto, t).getValores().isEmpty() && form(reabierto, t).getErrores().isEmpty()
                && form(reabierto, t).getIntento() == 0, "reabre vacío");
            var cancelado = ParticipacionUiStateKt.cerrarParticipacion(fill(reabierto, t, "demo@example.test", ""));
            check(cancelado.getPostulacion().getValores().isEmpty() && cancelado.getAporte().getValores().isEmpty(),
                "cancelar descarta ambos borradores");
        }
        var aporte = ParticipacionUiStateKt.abrirParticipacion(initial(p), TipoParticipacion.APORTE);
        check(ParticipacionUiStateKt.enviarParticipacion(fill(aporte, TipoParticipacion.APORTE, "demo@example.test", ""),
            TipoParticipacion.APORTE).getModal() == ModalParticipacion.EXITO_APORTE, "fuente opcional");
        var post = ParticipacionUiStateKt.abrirParticipacion(initial(p), TipoParticipacion.POSTULACION);
        check(change(post, TipoParticipacion.APORTE, CampoParticipacion.NOMBRE, "Ajeno") == post, "estados separados");
        check(change(post, TipoParticipacion.POSTULACION, CampoParticipacion.FUENTE, "Ajeno") == post, "campo ajeno ignorado");
        check(ParticipacionUiStateKt.enviarParticipacion(post, TipoParticipacion.APORTE) == post, "no envía otro tipo");
        var closed = initial(p);
        check(ParticipacionUiStateKt.enviarParticipacion(closed, TipoParticipacion.POSTULACION) == closed,
            "cerrado no puede enviarse");
        var closedProject = ParticipacionUiStateKt.abrirParticipacion(initial(repo.getProyecto("proy-reciclaje")), TipoParticipacion.APORTE);
        check(closedProject.getModal() == ModalParticipacion.APORTE, "sin cupos sí permite aporte");
        var urlSource = ParticipacionUiStateKt.enviarParticipacion(fill(aporte, TipoParticipacion.APORTE,
            "demo@example.test", "https://example.test/referencia"), TipoParticipacion.APORTE);
        check(urlSource.getModal() == ModalParticipacion.EXITO_APORTE, "fuente URL también permitida sin abrirla");
        var sameIdDifferentDraft = ParticipacionUiStateKt.abrirParticipacion(initial(p), TipoParticipacion.POSTULACION);
        check(sameIdDifferentDraft.getPostulacion().getValores().isEmpty(), "nuevo estado no hereda datos personales");
        check(ParticipacionUiStateKt.abrirParticipacion(initial(null), TipoParticipacion.APORTE).getModal()
            == ModalParticipacion.CERRADO, "proyecto inexistente no abre");
        try (var apk = new ZipFile("app/build/outputs/apk/debug/app-debug.apk")) {
            for (var id : List.of("proy-reciclaje", "proy-energia")) {
                var recursos = repo.getRecursos(id);
                check(recursos.size() == 1 && recursos.get(0).getProyectoId().equals(id), "recurso propio por proyecto");
                var nombre = id.equals("proy-reciclaje") ? "nota_reciclaje" : "nota_energia";
                var entry = apk.getEntry("res/raw/" + nombre + ".txt");
                check(entry != null && recursos.get(0).getContenidoRes() != 0, "documento empaquetado con ID real");
                try (var in = apk.getInputStream(entry)) {
                    String texto = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                    check(texto.contains(id) && texto.contains("DEMOSTRACIÓN"), "texto legible y marcado como demostración");
                }
            }
            check(apk.getEntry("assets/licenses/Inter-OFL.txt") != null
                && apk.getEntry("assets/licenses/PlusJakartaSans-OFL.txt") != null, "licencias incluidas en APK");
        }
        System.out.println(checks + " comprobaciones de historia 2/participación/recursos correctas; no prueban UI.");
    }
}
