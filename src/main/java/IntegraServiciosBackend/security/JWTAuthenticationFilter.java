package IntegraServiciosBackend.security;

import IntegraServiciosBackend.service.imp.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    UsuarioService usuarioService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        System.out.println("attemptAuthentication");
        AuthCredentials authCredentials = new AuthCredentials();

        try{
            authCredentials = new ObjectMapper().readValue(request.getReader(),AuthCredentials.class);
        }catch (IOException e){

        }

        System.out.println(authCredentials);

        UsernamePasswordAuthenticationToken usernamePAT = new UsernamePasswordAuthenticationToken(
                authCredentials.getEmail(),
                authCredentials.getContraseña()
        );

        System.out.println(usernamePAT);

        return getAuthenticationManager().authenticate(usernamePAT);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        String token = TokenUtils.createToken(userDetails.getNombre(),userDetails.getUsername());

        System.out.println("Token JWT generado: " + token);

        // Configurar el encabezado de autorización
        response.addHeader("Authorization", "Bearer " + token);

        // Construir el cuerpo de la respuesta
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("id", userDetails.getId());
        responseBody.put("nombre", userDetails.getNombre());
        responseBody.put("cedula", userDetails.getCedula());
        responseBody.put("email", userDetails.getUsername());
        responseBody.put("rol", userDetails.getAuthorities().iterator().next().getAuthority());
        responseBody.put("token", "Bearer "+ token);
        // Agrega más información si es necesario

        // Convertir el cuerpo de la respuesta a JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBodyJson = objectMapper.writeValueAsString(responseBody);

        // Configurar el encabezado de respuesta para indicar el tipo de contenido
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Escribir el cuerpo de la respuesta en el HttpServletResponse
        response.getWriter().write(responseBodyJson);
        response.getWriter().flush();

        System.out.println("successfulAuthentication");

        super.successfulAuthentication(request, response, chain, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        // Construir el cuerpo de la respuesta
        Map<String, Object> responseBody = new HashMap<>();

        //usuarioService.buscarUsuarioPorEmail()

        responseBody.put("error", "Autenticación inválida");
        responseBody.put("mensaje", "Valide su correo y contraseña");

        // Convertir el cuerpo de la respuesta a JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBodyJson = objectMapper.writeValueAsString(responseBody);

        // Configurar el encabezado de respuesta para indicar el tipo de contenido
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Escribir el cuerpo de la respuesta en el HttpServletResponse
        response.getWriter().write(responseBodyJson);
        response.getWriter().flush();
    }
}
