-- Create databases for microservices
CREATE DATABASE IF NOT EXISTS auction_db;
CREATE DATABASE IF NOT EXISTS user_db;
CREATE DATABASE IF NOT EXISTS payment_db;
CREATE DATABASE IF NOT EXISTS analytics_db;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE auction_db TO auction_user;
GRANT ALL PRIVILEGES ON DATABASE user_db TO auction_user;
GRANT ALL PRIVILEGES ON DATABASE payment_db TO auction_user;
GRANT ALL PRIVILEGES ON DATABASE analytics_db TO auction_user;