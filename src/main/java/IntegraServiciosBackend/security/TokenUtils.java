package IntegraServiciosBackend.security;

import IntegraServiciosBackend.utils.JsonPrinter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.*;
@Component
@AllArgsConstructor
public class TokenUtils {
    private static String ACCESS_TOKEN_SECRET = "ro7EeEuspEvbEUJK1o7QLMvsrYD72hfopDdo34lBbR2OdRAm2rqiFWmy04flLtXG";
    private static Long ACCESS_TOKEN_VALIDITY_SECONDS = 2_592_000L;

    UserDetailServiceImpl userDetailsService;
    public static String createToken(String nombre, String email){
        long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS * 1_000;
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        Map<String,Object> extra = new HashMap<>();
        extra.put("nombre",nombre);

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(expirationDate)
                .addClaims(extra)
                .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
                .compact();
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes())) 
                .build()
                .parseSignedClaims(token) 
                .getPayload();

            String email = claims.getSubject();
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            System.out.println("Autenticando");
            System.out.println(JsonPrinter.toString(userDetails.getAuthorities()));

            return new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());
        } catch (JwtException e) {
            return null;
        }
    }
}
