package br.gov.inep.censo.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testes unitarios de PasswordUtil (PBKDF2 e SHA-256 legado).
 */
public class PasswordUtilTest {

    @Test
    public void hashPassword_retornaHashPbkdf2ComPrefixo() {
        String hash = PasswordUtil.hashPassword("minhaS3nha");
        assertNotNull(hash);
        assertTrue("Hash deve iniciar com PBKDF2$", hash.startsWith("PBKDF2$"));
    }

    @Test
    public void hashPassword_hashsDiferentesParaMesmaSenha() {
        String h1 = PasswordUtil.hashPassword("senha123");
        String h2 = PasswordUtil.hashPassword("senha123");
        assertNotEquals("Dois hashes da mesma senha devem ser diferentes (salt aleatorio)", h1, h2);
    }

    @Test
    public void verifyPassword_pbkdf2_senhaCorretaRetornaTrue() {
        String hash = PasswordUtil.hashPassword("correta");
        assertTrue(PasswordUtil.verifyPassword("correta", hash));
    }

    @Test
    public void verifyPassword_pbkdf2_senhaErradaRetornaFalse() {
        String hash = PasswordUtil.hashPassword("correta");
        assertFalse(PasswordUtil.verifyPassword("errada", hash));
    }

    @Test
    public void verifyPassword_sha256Legado_senhaCorretaRetornaTrue() {
        String sha256Hash = PasswordUtil.sha256("legado");
        assertTrue(PasswordUtil.verifyPassword("legado", sha256Hash));
    }

    @Test
    public void verifyPassword_sha256Legado_senhaErradaRetornaFalse() {
        String sha256Hash = PasswordUtil.sha256("legado");
        assertFalse(PasswordUtil.verifyPassword("errada", sha256Hash));
    }

    @Test
    public void verifyPassword_hashNuloRetornaFalse() {
        assertFalse(PasswordUtil.verifyPassword("qualquer", null));
    }

    @Test
    public void verifyPassword_hashVazioRetornaFalse() {
        assertFalse(PasswordUtil.verifyPassword("qualquer", ""));
    }

    @Test
    public void needsRehash_hashSha256RetornaTrue() {
        String sha256Hash = PasswordUtil.sha256("senha");
        assertTrue(PasswordUtil.needsRehash(sha256Hash));
    }

    @Test
    public void needsRehash_hashPbkdf2RetornaFalse() {
        String pbkdf2Hash = PasswordUtil.hashPassword("senha");
        assertFalse(PasswordUtil.needsRehash(pbkdf2Hash));
    }

    @Test
    public void sha256_mesmaSenhaProduceMesmoHash() {
        String h1 = PasswordUtil.sha256("fixo");
        String h2 = PasswordUtil.sha256("fixo");
        assertEquals(h1, h2);
    }

    @Test
    public void sha256_senshalNulaRetornaHashDaStringVazia() {
        String hashNulo = PasswordUtil.sha256(null);
        String hashVazio = PasswordUtil.sha256("");
        assertEquals(hashVazio, hashNulo);
    }
}
