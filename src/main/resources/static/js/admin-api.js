/**
 * ONG Patinhas - chamadas fetch autenticadas ao /api/v1/admin/** com protecao CSRF.
 *
 * O token e lido da meta tag _csrf (renderizada nas paginas admin) e enviado no header
 * X-CSRF-TOKEN exigido pelo Spring Security para requisicoes mutantes via sessao.
 */
(function () {
    'use strict';

    var CSRF_META = 'meta[name="_csrf"]';
    var CSRF_HEADER_META = 'meta[name="_csrf_header"]';
    var DEFAULT_CSRF_HEADER = 'X-CSRF-TOKEN';

    function getCsrfToken() {
        var meta = document.querySelector(CSRF_META);
        return meta ? meta.getAttribute('content') : null;
    }

    function getCsrfHeaderName() {
        var meta = document.querySelector(CSRF_HEADER_META);
        return meta ? meta.getAttribute('content') : DEFAULT_CSRF_HEADER;
    }

    function adminFetch(url, options) {
        options = options || {};
        var headers = new Headers(options.headers || {});
        var method = (options.method || 'GET').toUpperCase();

        if (method !== 'GET' && method !== 'HEAD' && method !== 'OPTIONS') {
            var token = getCsrfToken();
            if (token) {
                headers.set(getCsrfHeaderName(), token);
            }
        }

        options.headers = headers;
        options.credentials = options.credentials || 'same-origin';

        return fetch(url, options);
    }

    window.PatinhasAdmin = {
        fetch: adminFetch,
        getCsrfToken: getCsrfToken
    };
})();
