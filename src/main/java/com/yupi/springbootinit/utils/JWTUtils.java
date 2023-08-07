package com.yupi.springbootinit.utils;

import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWTUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.springbootinit.model.dto.user.UserTokenDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lanshu
 * @date 2023-08-03
 */
public class JWTUtils {
    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    // 私钥
    private static final String TOKEN_SECRET = "tuzki";

    /**
     * 生成token，自定义过期时间 毫秒
     *
     * @param userTokenDTO
     * @return
     */
    public static String generateToken(UserTokenDTO userTokenDTO) {
        try {
            // 私钥和加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            // 设置头部信息
            Map<String, Object> header = new HashMap<>(2);
            header.put("Type", "Jwt");
            header.put("alg", "HS256");

            return JWT.create()
                    .withHeader(header)
                    .withClaim("token", JSONUtil.toJsonStr(userTokenDTO))
                    //.withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            logger.error("generate token occur error, error is:{}", e);
            return null;
        }
    }

    /**
     * 检验token是否正确
     *
     * @param token
     * @return
     */
    public static UserTokenDTO parseToken(String token) {
        // 使用HMAC256算法和给定的密钥（TOKEN_SECRET）创建了一个JWT算法对象。
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        // 使用上一步创建的算法对象构建了一个JWT验证器。
        JWTVerifier verifier = JWT.require(algorithm).build();
        // 使用验证器来验证给定的令牌（token），并将验证结果返回为一个被解码的JWT对象。
        DecodedJWT jwt = verifier.verify(token);
        // 获取JWT对象中名为"token"的声明（claim）并将其作为字符串返回。
        String tokenInfo = jwt.getClaim("token").asString();
        try {
            // 解析JSON字符串并将其转换为指定的类型
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(tokenInfo, UserTokenDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
