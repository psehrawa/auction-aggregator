# Contributing to Auction Aggregator Platform

Thank you for your interest in contributing to the Auction Aggregator Platform! This document provides guidelines and instructions for contributing.

## Table of Contents
- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Making Contributions](#making-contributions)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Pull Request Process](#pull-request-process)

## Code of Conduct

We are committed to providing a welcoming and inclusive environment. Please read and follow our Code of Conduct:

- Be respectful and inclusive
- Welcome newcomers and help them get started
- Focus on constructive criticism
- Respect differing opinions and experiences

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/yourusername/auction-aggregator.git`
3. Add upstream remote: `git remote add upstream https://github.com/original/auction-aggregator.git`
4. Create a new branch: `git checkout -b feature/your-feature-name`

## Development Setup

### Prerequisites
- Java 17+
- Node.js 18+
- Docker and Docker Compose
- PostgreSQL 14+
- Redis 7+

### Backend Setup
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

### Full Stack Setup
```bash
docker-compose up -d
```

## Making Contributions

### Types of Contributions

1. **Bug Fixes**: Fix issues reported in GitHub Issues
2. **Features**: Implement new features from the roadmap
3. **Documentation**: Improve or add documentation
4. **Tests**: Add missing tests or improve test coverage
5. **Performance**: Optimize code for better performance

### Contribution Workflow

1. Check existing issues or create a new one
2. Fork and clone the repository
3. Create a feature branch
4. Make your changes
5. Write/update tests
6. Update documentation
7. Submit a pull request

## Coding Standards

### Java (Backend)

```java
// Use meaningful variable names
private BigDecimal calculateMinimumBid(Auction auction) {
    BigDecimal currentPrice = auction.getCurrentPrice();
    BigDecimal increment = auction.getBidIncrement();
    return currentPrice.add(increment);
}

// Document public methods
/**
 * Places a bid on an auction.
 * 
 * @param auctionId the ID of the auction
 * @param bidAmount the bid amount
 * @return the created bid
 * @throws BidException if the bid is invalid
 */
public Bid placeBid(String auctionId, BigDecimal bidAmount) {
    // Implementation
}
```

### TypeScript (Frontend)

```typescript
// Use interfaces for type safety
interface AuctionProps {
  auction: Auction;
  onBid: (amount: number) => void;
}

// Use functional components with hooks
const AuctionCard: FC<AuctionProps> = ({ auction, onBid }) => {
  const [bidAmount, setBidAmount] = useState(0);
  
  // Component logic
};

// Document complex functions
/**
 * Formats currency values for display
 * @param amount - The amount to format
 * @param currency - The currency code (default: INR)
 * @returns Formatted currency string
 */
export const formatCurrency = (amount: number, currency = 'INR'): string => {
  // Implementation
};
```

### General Guidelines

- Use descriptive variable and function names
- Keep functions small and focused
- Write self-documenting code
- Add comments for complex logic
- Follow existing code patterns

## Testing Guidelines

### Backend Testing

```java
@Test
@DisplayName("Should place bid successfully when bid is valid")
void placeBid_ValidBid_Success() {
    // Given
    Auction auction = createActiveAuction();
    PlaceBidDTO bidDTO = new PlaceBidDTO(new BigDecimal("1000"));
    
    // When
    BidDTO result = biddingService.placeBid(auction.getId(), bidDTO, "user123");
    
    // Then
    assertThat(result).isNotNull();
    assertThat(result.getAmount()).isEqualTo(new BigDecimal("1000"));
}
```

### Frontend Testing

```typescript
describe('AuctionCard', () => {
  it('should display auction details correctly', () => {
    const auction = mockAuction();
    
    render(<AuctionCard auction={auction} />);
    
    expect(screen.getByText(auction.title)).toBeInTheDocument();
    expect(screen.getByText(formatCurrency(auction.currentPrice))).toBeInTheDocument();
  });
});
```

### Test Coverage Requirements
- Backend: Minimum 80% coverage
- Frontend: Minimum 70% coverage
- All new features must include tests

## Pull Request Process

### Before Submitting

1. **Update your branch**:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Run tests**:
   ```bash
   # Backend
   cd backend
   ./mvnw test
   
   # Frontend
   cd frontend
   npm test
   ```

3. **Check code style**:
   ```bash
   # Backend
   ./mvnw spotless:check
   
   # Frontend
   npm run lint
   ```

### PR Guidelines

1. **Title**: Use a clear, descriptive title
   - ✅ "Add proxy bidding feature to auction service"
   - ❌ "Fixed stuff"

2. **Description**: Include:
   - What changes were made
   - Why the changes were necessary
   - How to test the changes
   - Screenshots (for UI changes)

3. **Checklist**:
   - [ ] Tests pass locally
   - [ ] Code follows style guidelines
   - [ ] Documentation updated
   - [ ] No breaking changes (or documented)
   - [ ] Tested on different browsers (frontend)

### PR Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing completed

## Screenshots (if applicable)
Add screenshots here

## Related Issues
Fixes #123
```

### Review Process

1. At least one maintainer review required
2. All CI checks must pass
3. No merge conflicts
4. Address review feedback promptly

## Branch Naming

- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation changes
- `refactor/` - Code refactoring
- `test/` - Test additions/changes
- `perf/` - Performance improvements

Examples:
- `feature/add-proxy-bidding`
- `fix/websocket-connection-issue`
- `docs/update-api-documentation`

## Commit Messages

Follow the Conventional Commits specification:

```
<type>(<scope>): <subject>

<body>

<footer>
```

Examples:
```
feat(auction): add proxy bidding support

Implemented automatic bidding up to a maximum amount.
Users can now set a maximum bid and the system will
automatically bid on their behalf.

Closes #234
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes
- `refactor`: Code refactoring
- `perf`: Performance improvements
- `test`: Test additions/changes
- `chore`: Build process or auxiliary tool changes

## Questions?

Feel free to:
- Open an issue for discussion
- Join our Discord server
- Email: contribute@auctionaggregator.com

Thank you for contributing!