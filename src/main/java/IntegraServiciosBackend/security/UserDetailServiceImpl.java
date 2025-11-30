package IntegraServiciosBackend.security;

import IntegraServiciosBackend.entity.Usuario;
import IntegraServiciosBackend.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Buscando usuario con email " +email);
        Usuario usuario = usuarioRepository
                .findOneByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("El usuario con correo "+email+" no existe."));


        return new UserDetailsImpl(usuario);
    }
}
