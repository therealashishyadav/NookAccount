//package com.Account.Services;
//
//
//import java.util.Date;
//import java.util.Map;
//import java.security.Key;
//import java.util.function.Function;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import com.Account.Model.User;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//
//@Service
//public class JwtServiceImpl implements JwtService{
//	
////	public String generateToken(UserDetails userDetails) {
////		return Jwts.builder().setSubject(userDetails.getUsername())
////				.setIssuedAt(new Date(System.currentTimeMillis()))
////				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
////				.signWith(getSigninKey(), SignatureAlgorithm.HS256).compact();
////	}
//	
//	public String generateToken(UserDetails userDetails) {
//	    User user = (User) userDetails;
//	    return Jwts.builder()
//	            .setSubject(userDetails.getUsername())
//	            .claim("userId", user.getId())
//	            .claim("role", user.getRole().name())
//	            .setIssuedAt(new Date(System.currentTimeMillis()))
//	            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
//	            .signWith(getSigninKey(), SignatureAlgorithm.HS256).compact();
//	}
//	
////	refersh
//	
//	public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
//		return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
//				.setIssuedAt(new Date(System.currentTimeMillis()))
//				.setExpiration(new Date(System.currentTimeMillis() + 604800000))
//				.signWith(getSigninKey(), SignatureAlgorithm.HS256).compact();
//	}
//
//	
//	private Key getSigninKey() {
//	    byte[] key = Decoders.BASE64.decode("q4JjknVjndVi5B1u6WqBl+S3rXWk4Hrp12qFZRfTcys=");
//	    return Keys.hmacShaKeyFor(key);
//	}
//
//	
//	private <T> T extractClaim(String token, Function<Claims, T> claimResolvers) {
//		final Claims claims = extractAllClaims(token);
//		return claimResolvers.apply(claims);
//	}
//		
//	private Claims extractAllClaims(String token) {
//		return Jwts.parserBuilder().setSigningKey(getSigninKey()).build().parseClaimsJws(token).getBody();
//	}
//	
//	public String extractUserName(String token) {
//		return extractClaim(token, Claims::getSubject);
//	}
//	
//	public boolean isTokenValid(String token, UserDetails userDetails) {
//		final String username = extractUserName(token);
//		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//	}
//	
//	public boolean isTokenExpired(String token) {
//		return extractClaim(token, Claims::getExpiration).before(new Date());
//	}
//
//
//
//}
//


package com.Account.Services;

import java.util.Date;
import java.util.Map;
import java.security.Key;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.Account.Model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {

    public String generateToken(UserDetails userDetails) {
        User user = (User) userDetails;
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("userId", user.getId())         // ← embed userId
                .claim("role", user.getRole().name())  // ← embed role
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 604800000))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigninKey() {
        byte[] key = Decoders.BASE64.decode("q4JjknVjndVi5B1u6WqBl+S3rXWk4Hrp12qFZRfTcys=");
        return Keys.hmacShaKeyFor(key);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolvers) {
        return claimResolvers.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigninKey()).build().parseClaimsJws(token).getBody();
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}