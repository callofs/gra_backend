package com.graProject.graBackend.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.graProject.graBackend.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类。
 *
 * 用于生成、校验和解析系统登录令牌，令牌默认有效期为 6 小时。
 */
@Component
public class JwtTokenUtil {

    /**
     * JWT 默认过期时间，单位为毫秒。
     */
    private static final long EXPIRE_MILLIS = 6 * 60 * 60 * 1000L;

    /**
     * 用户 ID 载荷字段。
     */
    private static final String CLAIM_USER_ID = "userId";
    /**
     * 用户名载荷字段。
     */
    private static final String CLAIM_USERNAME = "username";
    /**
     * 用户昵称载荷字段。
     */
    private static final String CLAIM_NICKNAME = "nickname";
    /**
     * 用户头像载荷字段。
     */
    private static final String CLAIM_AVATAR = "avatar";
    /**
     * 用户邮箱载荷字段。
     */
    private static final String CLAIM_EMAIL = "email";
    /**
     * 用户手机号载荷字段。
     */
    private static final String CLAIM_PHONE = "phone";
    /**
     * 用户角色载荷字段。
     */
    private static final String CLAIM_ROLE = "role";
    /**
     * 用户状态载荷字段。
     */
    private static final String CLAIM_STATUS = "status";

    /**
     * JWT 签名密钥。
     */
    private final String secret;

    /**
     * 构造方法。
     *
     * @param secret JWT 签名密钥
     */
    public JwtTokenUtil(
            @Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    /**
     * 根据用户基础信息生成 JWT。
     *
     * @param userDTO 用户基础信息
     * @return 已签名的 JWT 字符串
     */
    public String generateToken(UserDTO userDTO) {
        if (userDTO == null || userDTO.getId() == null || !StringUtils.hasText(userDTO.getUsername())) {
            throw new IllegalArgumentException("用户信息不能为空");
        }

        Instant now = Instant.now();
        Instant expireAt = now.plusMillis(EXPIRE_MILLIS);

        return JWT.create()
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expireAt))
                .withClaim(CLAIM_USER_ID, userDTO.getId())
                .withClaim(CLAIM_USERNAME, userDTO.getUsername())
                .withClaim(CLAIM_NICKNAME, userDTO.getNickname())
                .withClaim(CLAIM_AVATAR, userDTO.getAvatar())
                .withClaim(CLAIM_EMAIL, userDTO.getEmail())
                .withClaim(CLAIM_PHONE, userDTO.getPhone())
                .withClaim(CLAIM_ROLE, userDTO.getRole())
                .withClaim(CLAIM_STATUS, userDTO.getStatus())
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 校验 JWT 是否合法且未过期。
     *
     * @param token JWT 字符串
     * @return 合法返回 true，否则返回 false
     */
    public boolean verifyToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        try {
            buildVerifier().verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    /**
     * 解析并校验 JWT。
     *
     * @param token JWT 字符串
     * @return 解析后的 JWT 对象
     */
    public DecodedJWT parseToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("token不能为空");
        }
        return buildVerifier().verify(token);
    }

    /**
     * 从 JWT 中解析用户基础信息。
     *
     * @param token JWT 字符串
     * @return 用户基础信息
     */
    public UserDTO parseUserInfo(String token) {
        DecodedJWT decodedJWT = parseToken(token);
        return UserDTO.builder()
                .id(readLongClaim(decodedJWT, CLAIM_USER_ID))
                .username(readStringClaim(decodedJWT, CLAIM_USERNAME))
                .nickname(readStringClaim(decodedJWT, CLAIM_NICKNAME))
                .avatar(readStringClaim(decodedJWT, CLAIM_AVATAR))
                .email(readStringClaim(decodedJWT, CLAIM_EMAIL))
                .phone(readStringClaim(decodedJWT, CLAIM_PHONE))
                .role(readIntegerClaim(decodedJWT, CLAIM_ROLE))
                .status(readIntegerClaim(decodedJWT, CLAIM_STATUS))
                .build();
    }

    /**
     * 获取 JWT 过期时间。
     *
     * @param token JWT 字符串
     * @return 过期时间
     */
    public Date getExpireTime(String token) {
        return parseToken(token).getExpiresAt();
    }

    /**
     * 解析 JWT 中的全部业务载荷。
     *
     * @param token JWT 字符串
     * @return 载荷键值对
     */
    public Map<String, Object> parseClaims(String token) {
        DecodedJWT decodedJWT = parseToken(token);
        Map<String, Claim> claims = decodedJWT.getClaims();
        Map<String, Object> result = new HashMap<>();
        result.put(CLAIM_USER_ID, readLongClaim(decodedJWT, CLAIM_USER_ID));
        result.put(CLAIM_USERNAME, readStringClaim(decodedJWT, CLAIM_USERNAME));
        result.put(CLAIM_NICKNAME, readStringClaim(decodedJWT, CLAIM_NICKNAME));
        result.put(CLAIM_AVATAR, readStringClaim(decodedJWT, CLAIM_AVATAR));
        result.put(CLAIM_EMAIL, readStringClaim(decodedJWT, CLAIM_EMAIL));
        result.put(CLAIM_PHONE, readStringClaim(decodedJWT, CLAIM_PHONE));
        result.put(CLAIM_ROLE, readIntegerClaim(decodedJWT, CLAIM_ROLE));
        result.put(CLAIM_STATUS, readIntegerClaim(decodedJWT, CLAIM_STATUS));
        result.put("issuedAt", decodedJWT.getIssuedAt());
        result.put("expiresAt", decodedJWT.getExpiresAt());
        result.put("rawClaims", claims);
        return result;
    }

    private JWTVerifier buildVerifier() {
        return JWT.require(Algorithm.HMAC256(secret)).build();
    }

    private String readStringClaim(DecodedJWT decodedJWT, String claimName) {
        Claim claim = decodedJWT.getClaim(claimName);
        return claim == null ? null : claim.asString();
    }

    private Long readLongClaim(DecodedJWT decodedJWT, String claimName) {
        Claim claim = decodedJWT.getClaim(claimName);
        return claim == null ? null : claim.asLong();
    }

    private Integer readIntegerClaim(DecodedJWT decodedJWT, String claimName) {
        Claim claim = decodedJWT.getClaim(claimName);
        return claim == null ? null : claim.asInt();
    }
}
