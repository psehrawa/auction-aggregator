-- Create categories table
CREATE TABLE categories (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    slug VARCHAR(255) UNIQUE,
    description TEXT,
    image_url VARCHAR(500),
    parent_id VARCHAR(36),
    level INTEGER DEFAULT 0 NOT NULL,
    display_order INTEGER DEFAULT 0 NOT NULL,
    featured BOOLEAN DEFAULT FALSE NOT NULL,
    metadata JSONB,
    active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (parent_id) REFERENCES categories(id)
);

CREATE INDEX idx_categories_parent ON categories(parent_id);
CREATE INDEX idx_categories_slug ON categories(slug);
CREATE INDEX idx_categories_featured ON categories(featured) WHERE featured = true;

-- Create auctions table
CREATE TABLE auctions (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    seller_id VARCHAR(36) NOT NULL,
    category_id VARCHAR(36),
    status VARCHAR(50) NOT NULL,
    auction_type VARCHAR(50) NOT NULL,
    starting_price DECIMAL(19,2) NOT NULL,
    reserve_price DECIMAL(19,2),
    current_price DECIMAL(19,2),
    buy_now_price DECIMAL(19,2),
    bid_increment DECIMAL(19,2) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    actual_end_time TIMESTAMP,
    auto_extend BOOLEAN DEFAULT FALSE NOT NULL,
    auto_extend_minutes INTEGER DEFAULT 5,
    view_count INTEGER DEFAULT 0 NOT NULL,
    watcher_count INTEGER DEFAULT 0 NOT NULL,
    winner_id VARCHAR(36),
    winning_bid DECIMAL(19,2),
    source VARCHAR(100) NOT NULL,
    external_id VARCHAR(255),
    external_url VARCHAR(500),
    active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE INDEX idx_auctions_seller ON auctions(seller_id);
CREATE INDEX idx_auctions_status ON auctions(status);
CREATE INDEX idx_auctions_category ON auctions(category_id);
CREATE INDEX idx_auctions_end_time ON auctions(end_time);
CREATE INDEX idx_auctions_start_time ON auctions(start_time);
CREATE INDEX idx_auctions_source ON auctions(source);
CREATE INDEX idx_auctions_external_id ON auctions(external_id);
CREATE INDEX idx_auctions_active_ending ON auctions(end_time) WHERE status IN ('ACTIVE', 'ENDING_SOON');

-- Create bids table
CREATE TABLE bids (
    id VARCHAR(36) PRIMARY KEY,
    auction_id VARCHAR(36) NOT NULL,
    bidder_id VARCHAR(36) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    max_amount DECIMAL(19,2),
    bid_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    bid_time TIMESTAMP NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    device_id VARCHAR(255),
    is_proxy_bid BOOLEAN DEFAULT FALSE NOT NULL,
    parent_bid_id VARCHAR(36),
    cancellation_reason TEXT,
    cancellation_time TIMESTAMP,
    is_winning_bid BOOLEAN DEFAULT FALSE NOT NULL,
    source VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (auction_id) REFERENCES auctions(id),
    FOREIGN KEY (parent_bid_id) REFERENCES bids(id)
);

CREATE INDEX idx_bids_auction ON bids(auction_id);
CREATE INDEX idx_bids_bidder ON bids(bidder_id);
CREATE INDEX idx_bids_bid_time ON bids(bid_time);
CREATE INDEX idx_bids_status ON bids(status);
CREATE INDEX idx_bids_winning ON bids(auction_id, is_winning_bid) WHERE is_winning_bid = true;

-- Create auction_images table
CREATE TABLE auction_images (
    id VARCHAR(36) PRIMARY KEY,
    auction_id VARCHAR(36) NOT NULL,
    url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    title VARCHAR(255),
    display_order INTEGER DEFAULT 0 NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE NOT NULL,
    width INTEGER,
    height INTEGER,
    size_bytes BIGINT,
    active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE CASCADE
);

CREATE INDEX idx_auction_images_auction ON auction_images(auction_id);
CREATE INDEX idx_auction_images_primary ON auction_images(auction_id, is_primary) WHERE is_primary = true;

-- Create auction_details table
CREATE TABLE auction_details (
    id VARCHAR(36) PRIMARY KEY,
    auction_id VARCHAR(36) NOT NULL UNIQUE,
    condition VARCHAR(50),
    location_city VARCHAR(255),
    location_state VARCHAR(255),
    location_country VARCHAR(100),
    location_zip VARCHAR(20),
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    shipping_available BOOLEAN DEFAULT TRUE,
    shipping_cost DECIMAL(19,2),
    shipping_info TEXT,
    inspection_available BOOLEAN DEFAULT FALSE,
    inspection_dates TEXT,
    warranty_info TEXT,
    return_policy TEXT,
    additional_info JSONB,
    active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE CASCADE
);

-- Create auction_tags table
CREATE TABLE auction_tags (
    auction_id VARCHAR(36) NOT NULL,
    tag VARCHAR(100) NOT NULL,
    PRIMARY KEY (auction_id, tag),
    FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE CASCADE
);

CREATE INDEX idx_auction_tags_tag ON auction_tags(tag);

-- Create auction_history table
CREATE TABLE auction_history (
    id VARCHAR(36) PRIMARY KEY,
    auction_id VARCHAR(36) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    action_by VARCHAR(36) NOT NULL,
    action_details TEXT,
    old_value JSONB,
    new_value JSONB,
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (auction_id) REFERENCES auctions(id)
);

CREATE INDEX idx_auction_history_auction ON auction_history(auction_id);
CREATE INDEX idx_auction_history_action ON auction_history(action_type);
CREATE INDEX idx_auction_history_created ON auction_history(created_at);

-- Create watchlist table
CREATE TABLE watchlist (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    auction_id VARCHAR(36) NOT NULL,
    notify_on_bid BOOLEAN DEFAULT TRUE,
    notify_on_ending BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, auction_id),
    FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE CASCADE
);

CREATE INDEX idx_watchlist_user ON watchlist(user_id);
CREATE INDEX idx_watchlist_auction ON watchlist(auction_id);

-- Create category_attributes table
CREATE TABLE category_attributes (
    category_id VARCHAR(36) NOT NULL,
    attribute VARCHAR(255) NOT NULL,
    PRIMARY KEY (category_id, attribute),
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);