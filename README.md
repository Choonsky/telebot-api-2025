# Telegram Bot Api

## Requirements:
- Oracle DB or another SQL DB (change application.yaml)
- RabbitMQ (change application.yaml)
- Telegram Bot registered (change application.yaml)
- For CI/CD: config "Future CI/CD" folder files

## Protocol

HTTP request should have:

PARAMETERS

- source = 1..n - code of the system that have sent this request (0 - Zabbix, ...)
- severity in ("INFO", "WARNING", "ERROR", "FATAL", "PANIC") - severity level, used for user groups defining:
  - INFO - sending for DEBUGGERS users only
  - WARNING, ERROR - sending for DEBUGGERS and TECH_STAFF users
  - FATAL - sending for all registered users
  - PANIC - special
- serviceName - name of a service affected
- message - message text

USER GROUPS

- DEBUGGERS - receive ALL the messages
- TECH_STAFF - receives all the messages except INFO
- ADMINS - users that can change user groups
- ALL - users that receives FATAL and PANIC messages
- other / without a group - users counted in users lists but receiving nothing

COMMANDS

- /users - show users (all registered users)
- /user XX - show user id=XX groups (ADMINS only)
- /user XX add to YY - add group YY to user XX (ADMINS only)
- /user XX remove from YY - remove user XX from group YY (ADMINS only)
- /groups - show groups (ADMINS only)
- /start - hello text
- /help - list of allowed commands