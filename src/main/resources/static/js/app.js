/**
 * ONG Patinhas - JS principal
 *
 * Pequenas melhorias progressivas. O sistema funciona 100% sem JS,
 * mas com ele a navegação fica mais agradável.
 */
(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        autoOcultarAlertas();
        confirmarRemocoes();
    });

    function autoOcultarAlertas() {
        const alertas = document.querySelectorAll('.alerta--sucesso');
        alertas.forEach(function (el) {
            setTimeout(function () {
                el.style.transition = 'opacity 0.6s ease';
                el.style.opacity = '0';
                setTimeout(function () { el.remove(); }, 700);
            }, 4500);
        });
    }

    function confirmarRemocoes() {
        const forms = document.querySelectorAll('form[data-confirmar]');
        forms.forEach(function (f) {
            f.addEventListener('submit', function (e) {
                const msg = f.getAttribute('data-confirmar') || 'Tem certeza?';
                if (!window.confirm(msg)) {
                    e.preventDefault();
                }
            });
        });
    }
})();
