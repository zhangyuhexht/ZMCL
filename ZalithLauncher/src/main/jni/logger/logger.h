//
// Created by movte on 2025/4/25.
//

#ifndef ZMCL_LOGGER_H
#define ZMCL_LOGGER_H

#define LOG_E "ERROR"
#define LOG_W "WARN"
#define LOG_I "INFO"
#define LOG_D "DEBUG"

#define LOG_TO_E(...) zmcl_log(LOG_E, __VA_ARGS__)
#define LOG_TO_W(...) zmcl_log(LOG_W, __VA_ARGS__)
#define LOG_TO_I(...) zmcl_log(LOG_I, __VA_ARGS__)
#define LOG_TO_D(...) zmcl_log(LOG_D, __VA_ARGS__)

void zmcl_log(const char *level, const char *fmt, ...);

#endif // ZMCL_LOGGER_H
