#!/bin/bash

# Переменные окружения (будут переданы из docker-compose)
DB_HOST="postgres-db"  # Имя хоста из compose (hostname: postgres)
DB_PORT="5432"
DB_NAME="${POSTGRES_DATABASE}"
DB_USER="${POSTGRES_USERNAME}"
DB_PASSWORD="${POSTGRES_PASSWORD}"
S3_BUCKET="${S3_BUCKET}"
AWS_ACCESS_KEY_ID="${AWS_ACCESS_KEY_ID}"
AWS_SECRET_ACCESS_KEY="${AWS_SECRET_ACCESS_KEY}"
S3_ENDPOINT="https://s3.ru1.storage.beget.cloud"
BACKUP_DIR="/tmp"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/backup_${DB_NAME}_${TIMESTAMP}.sql.gz"

# Email-настройки:
EMAIL_TO="${MAIL_TO:-admin@example.com}"
EMAIL_FROM="${MAIL_FROM:-backup@yourserver.com}"
SMTP_SERVER="${MAIL_HOST}"
SMTP_PORT="${MAIL_PORT}"
SMTP_USER="${MAIL_USERNAME}"
SMTP_PASS="${MAIL_PASSWORD}"

# Экспортируем ключи для awscli (без профиля, используем переменные)
export AWS_ACCESS_KEY_ID
export AWS_SECRET_ACCESS_KEY

# Функция отправки email (для smtp протокола)
send_email() {
    local subject="$1"
    local body="$2"
    echo "$body" | mail -s "$subject" -a "From: $EMAIL_FROM" -S smtp="$SMTP_SERVER:$SMTP_PORT" -S smtp-auth=login -S smtp-auth-user="$SMTP_USER" -S smtp-auth-password="$SMTP_PASS" "$EMAIL_TO"
}

# Создадим дамп через сеть (используем pg_dump из контейнера)
if PGPASSWORD=$DB_PASSWORD pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME | gzip > $BACKUP_FILE; then
    echo "$(date): Дамп создан успешно: $BACKUP_FILE"
else
    send_email "Ошибка бэкапа: Дамп не удался" "Не удалось создать дамп базы $DB_NAME в $TIMESTAMP. Проверь логи и подключение к PostgreSQL."
    exit 1
fi

# Загрузим в S3
if aws s3 --endpoint-url=$S3_ENDPOINT cp $BACKUP_FILE s3://$S3_BUCKET/; then
    echo "$(date): Бэкап загружен в S3: $BACKUP_FILE"
else
    send_email "Ошибка бэкапа: Загрузка в S3 не удалась" "Не удалось загрузить $BACKUP_FILE в S3. Проверь ключи AWS и подключение."
    rm $BACKUP_FILE  # Очистим файл даже при ошибке
    exit 1
fi

# Ротация: удалим бэкапы старше 7 дней
aws s3 --endpoint-url=$S3_ENDPOINT rm s3://$S3_BUCKET/ --recursive --exclude "*" --include "backup_${DB_NAME}_$(date -d '7 days ago' +%Y%m%d)*"

# Очисти временный файл
rm $BACKUP_FILE

echo "$(date): Бэкап $BACKUP_FILE загружен в S3 и старые удалены."

# Уведомление об успехе
send_email "Бэкап успешен: $DB_NAME" "Бэкап базы $DB_NAME завершен успешно в $TIMESTAMP. Файл: $BACKUP_FILE загружен в S3."
