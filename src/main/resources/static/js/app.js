/**
 * ONG Patinhas - JS principal
 */
(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        autoOcultarAlertas();
        confirmarRemocoes();
        configurarCopiarPix();
        configurarModalAnimais();
        configurarModalEventos();
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

    function configurarCopiarPix() {
        document.querySelectorAll('.btn-copiar-pix').forEach(function (btn) {
            btn.addEventListener('click', function () {
                const pix = btn.getAttribute('data-pix');
                if (!pix) {
                    return;
                }
                navigator.clipboard.writeText(pix).then(function () {
                    const textoOriginal = btn.textContent;
                    btn.textContent = 'Copiado!';
                    setTimeout(function () {
                        btn.textContent = textoOriginal;
                    }, 2000);
                }).catch(function () {
                    window.prompt('Copie a chave PIX:', pix);
                });
            });
        });
    }

    function configurarModalAnimais() {
        const modal = document.getElementById('modal-animal');
        if (!modal) {
            return;
        }

        const whatsappNumero = modal.dataset.whatsapp || '';
        const cards = document.querySelectorAll('.card-animal--clicavel');

        cards.forEach(function (card) {
            card.addEventListener('click', function () {
                abrirModalAnimal(card, modal, whatsappNumero);
            });
            card.addEventListener('keydown', function (e) {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    abrirModalAnimal(card, modal, whatsappNumero);
                }
            });
        });

        modal.querySelectorAll('[data-fechar-modal]').forEach(function (el) {
            el.addEventListener('click', function () {
                fecharModalAnimal(modal);
            });
        });

        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape' && modal.classList.contains('modal-animal--aberto')) {
                fecharModalAnimal(modal);
            }
        });
    }

    function abrirModalAnimal(card, modal, whatsappNumero) {
        const nome = card.dataset.nome || '';
        const foto = card.dataset.foto || '';
        const descricao = card.dataset.descricao || 'Sem descrição disponível no momento.';

        document.getElementById('modal-animal-titulo').textContent = nome;
        document.getElementById('modal-animal-idade').textContent = (card.dataset.idade || '—') + ' anos';
        document.getElementById('modal-animal-porte').textContent = card.dataset.porte || '—';
        document.getElementById('modal-animal-sexo').textContent = card.dataset.sexo || '—';
        document.getElementById('modal-animal-descricao').textContent = descricao;
        document.getElementById('modal-animal-status').textContent = card.dataset.status || '—';
        document.getElementById('modal-whatsapp-nome').textContent = nome;

        const fotoEl = document.getElementById('modal-animal-foto');
        const placeholder = document.getElementById('modal-animal-placeholder');
        if (foto) {
            fotoEl.style.backgroundImage = "url('" + foto + "')";
            placeholder.hidden = true;
        } else {
            fotoEl.style.backgroundImage = 'none';
            placeholder.hidden = false;
        }

        document.getElementById('modal-animal-castrado-linha').hidden = card.dataset.castrado !== 'true';
        document.getElementById('modal-animal-vacinado-linha').hidden = card.dataset.vacinado !== 'true';

        const btnWhatsapp = document.getElementById('modal-whatsapp-btn');
        const aviso = document.getElementById('modal-whatsapp-aviso');
        const mensagem = 'Oi! tenho interesse em adotar ' + nome + '!';

        if (whatsappNumero) {
            btnWhatsapp.href = 'https://wa.me/' + whatsappNumero + '?text=' + encodeURIComponent(mensagem);
            btnWhatsapp.hidden = false;
            aviso.hidden = true;
        } else {
            btnWhatsapp.hidden = true;
            aviso.hidden = false;
        }

        modal.classList.add('modal-animal--aberto');
        modal.setAttribute('aria-hidden', 'false');
        document.body.classList.add('modal-aberto');
    }

    function fecharModalAnimal(modal) {
        modal.classList.remove('modal-animal--aberto');
        modal.setAttribute('aria-hidden', 'true');
        document.body.classList.remove('modal-aberto');
    }

    function configurarModalEventos() {
        const modal = document.getElementById('modal-evento');
        if (!modal) {
            return;
        }

        const cards = document.querySelectorAll('.card-evento--clicavel');

        cards.forEach(function (card) {
            card.addEventListener('click', function () {
                abrirModalEvento(card, modal);
            });
            card.addEventListener('keydown', function (e) {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    abrirModalEvento(card, modal);
                }
            });
        });

        modal.querySelectorAll('[data-fechar-modal-evento]').forEach(function (el) {
            el.addEventListener('click', function () {
                fecharModalEvento(modal);
            });
        });

        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape' && modal.classList.contains('modal-evento--aberto')) {
                fecharModalEvento(modal);
            }
        });
    }

    function abrirModalEvento(card, modal) {
        const titulo = card.dataset.titulo || '';
        const local = card.dataset.local || '—';
        const descricao = card.dataset.descricao || 'Sem descrição disponível.';
        const foto = card.dataset.foto || '';
        const data = card.dataset.data || '—';
        const hora = card.dataset.hora || '—';
        const dia = card.dataset.dia || '';
        const mes = (card.dataset.mes || '').toUpperCase();

        document.getElementById('modal-evento-titulo').textContent = titulo;
        document.getElementById('modal-evento-data').textContent = data;
        document.getElementById('modal-evento-hora').textContent = hora !== '—' ? 'às ' + hora : '—';
        document.getElementById('modal-evento-local').textContent = local;
        document.getElementById('modal-evento-descricao').textContent = descricao;

        const fotoEl = document.getElementById('modal-evento-foto');
        const placeholder = document.getElementById('modal-evento-placeholder');
        if (foto) {
            fotoEl.style.backgroundImage = "url('" + foto + "')";
            placeholder.hidden = true;
        } else {
            fotoEl.style.backgroundImage = 'none';
            document.getElementById('modal-evento-badge-dia').textContent = dia;
            document.getElementById('modal-evento-badge-mes').textContent = mes;
            placeholder.hidden = false;
        }

        modal.classList.add('modal-evento--aberto');
        modal.setAttribute('aria-hidden', 'false');
        document.body.classList.add('modal-aberto');
    }

    function fecharModalEvento(modal) {
        modal.classList.remove('modal-evento--aberto');
        modal.setAttribute('aria-hidden', 'true');
        document.body.classList.remove('modal-aberto');
    }
})();
