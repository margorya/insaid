package org.task.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtils {
    private static final String REG_EXP = "history (\\d+)";

    public static String parseTokenFromHeader(String token) {
        if (token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if (!token.startsWith("Bearer_")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String cleanToken = token.replace("Bearer_", "");
        if (cleanToken.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return cleanToken;
    }

    public static boolean isNeedHistory(String msg) {
        return Pattern.matches(REG_EXP, msg);
    }

    public static long getCountHistory(String msg) {
        Pattern pattern = Pattern.compile(REG_EXP);
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        throw  new IllegalArgumentException("Limit history not found");
    }
}
