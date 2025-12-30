package com.example.ElSilencio.modules.auth.service;

import com.example.ElSilencio.modules.auth.model.RolEnum;
import com.example.ElSilencio.modules.auth.model.TwoFactorCode;
import com.example.ElSilencio.modules.auth.model.UsuarioModel;
import com.example.ElSilencio.modules.auth.repository.TwoFactorCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TwoFactorService {

    private final TwoFactorCodeRepository twoFactorCodeRepository;
    private final EmailService emailService;

    @Transactional
    public String generarYEnviarCodigo(UsuarioModel usuario) {
        String codigo = String.format("%06d", new Random().nextInt(999999));
        TwoFactorCode twoFactorCode = new TwoFactorCode(usuario, codigo);
        twoFactorCodeRepository.save(twoFactorCode);
        emailService.enviarCodigo2FA(usuario.getEmail(), codigo);
        return codigo;
    }

    @Transactional
    public boolean verificarCodigo(UsuarioModel usuario, String codigo) {
        Optional<TwoFactorCode> twoFactorCode = twoFactorCodeRepository
                .findByUsuarioAndCodigoAndUsadoFalse(usuario, codigo);

        if (twoFactorCode.isPresent() && twoFactorCode.get().isValido()) {
            TwoFactorCode code = twoFactorCode.get();
            code.setUsado(true);
            twoFactorCodeRepository.save(code);
            return true;
        }
        return false;
    }

    public boolean requiere2FA(UsuarioModel usuario) {
        return usuario.getRol() == RolEnum.ADMINISTRADOR ||
                usuario.getRol() == RolEnum.RECEPCIONISTA;
    }
}
