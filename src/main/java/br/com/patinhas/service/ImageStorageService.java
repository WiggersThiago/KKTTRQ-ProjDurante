package br.com.patinhas.service;

import br.com.patinhas.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * Armazena imagens no diretório local do projeto e expõe caminhos públicos em /images/**.
 */
@Slf4j
@Service
public class ImageStorageService {

    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    @Value("${patinhas.upload.dir:images}")
    private String uploadDir;

    public String salvar(MultipartFile arquivo, String subpasta) {
        if (arquivo == null || arquivo.isEmpty()) {
            return null;
        }

        String contentType = arquivo.getContentType();
        if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType)) {
            throw new BusinessException("Formato de imagem não suportado. Use JPG, PNG, WEBP ou GIF.");
        }

        String extensao = obterExtensao(arquivo.getOriginalFilename(), contentType);
        String nomeArquivo = UUID.randomUUID() + extensao;

        try {
            Path destino = diretorioBase().resolve(subpasta).resolve(nomeArquivo);
            Files.createDirectories(destino.getParent());
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            String caminhoPublico = "/images/" + subpasta + "/" + nomeArquivo;
            log.info("Imagem salva em {}", caminhoPublico);
            return caminhoPublico;
        } catch (IOException e) {
            throw new BusinessException("Não foi possível salvar a imagem.");
        }
    }

    public void remover(String caminhoPublico) {
        if (!StringUtils.hasText(caminhoPublico)) {
            return;
        }
        try {
            Path arquivo = resolverArquivo(caminhoPublico);
            if (Files.deleteIfExists(arquivo)) {
                log.info("Imagem removida: {}", caminhoPublico);
            }
        } catch (IOException e) {
            log.warn("Falha ao remover imagem {}: {}", caminhoPublico, e.getMessage());
        }
    }

    public Path resolverArquivo(String caminhoPublico) {
        if (!StringUtils.hasText(caminhoPublico) || !caminhoPublico.startsWith("/images/")) {
            throw new BusinessException("Caminho de imagem inválido.");
        }
        String relativo = caminhoPublico.substring("/images/".length());
        Path arquivo = diretorioBase().resolve(relativo).normalize();
        if (!arquivo.startsWith(diretorioBase())) {
            throw new BusinessException("Caminho de imagem inválido.");
        }
        if (!Files.exists(arquivo)) {
            throw new BusinessException("Arquivo de imagem não encontrado.");
        }
        return arquivo;
    }

    public void substituir(String caminhoAntigo, String caminhoNovo) {
        if (StringUtils.hasText(caminhoAntigo) && !caminhoAntigo.equals(caminhoNovo)) {
            remover(caminhoAntigo);
        }
    }

    private Path diretorioBase() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    private String obterExtensao(String nomeOriginal, String contentType) {
        if (StringUtils.hasText(nomeOriginal)) {
            String ext = StringUtils.getFilenameExtension(nomeOriginal);
            if (StringUtils.hasText(ext)) {
                return "." + ext.toLowerCase();
            }
        }
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
    }
}
