package IntegraServiciosBackend.security;

import IntegraServiciosBackend.entity.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final Usuario usuario;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();

        if (usuario.getRol() == 1) {
            list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (usuario.getRol() == 2) {
            list.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else if (usuario.getRol() == 3) {
        list.add(new SimpleGrantedAuthority("ROLE_ALIADO"));
    }
        return list;
    }

    @Override
    public String getPassword() {
        return usuario.getContraseña();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getNombre(){
        return usuario.getFullname();
    }
    public Long getId(){
        return usuario.getId();
    }
    public int getCedula(){
        return usuario.getCedula();
    }
}
