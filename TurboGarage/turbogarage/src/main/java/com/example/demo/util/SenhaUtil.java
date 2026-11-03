package com.example.demo.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Classe responsável SOMENTE pela criptografia de senhas (BCrypt).
 */
public final class SenhaUtil {

    private SenhaUtil() {
    }

    // Transforma a senha em hash. O BCrypt gera um "salt" aleatório sozinho,
    // então a mesma senha gera hashes diferentes a cada vez (isso é normal).
    public static String criptografar(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    // Confere se a senha digitada bate com o hash salvo no banco.
    // Não dá para usar equals() por causa do salt: o checkpw refaz a conta corretamente.
    public static boolean conferir(String senhaDigitada, String hashSalvo) {
        if (senhaDigitada == null || hashSalvo == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(senhaDigitada, hashSalvo);
        } catch (IllegalArgumentException e) {
            // hash no banco em formato inválido
            return false;
        }
    }
}
