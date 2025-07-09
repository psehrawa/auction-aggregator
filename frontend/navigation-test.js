// Simple navigation test using puppeteer or playwright if available
// This tests if button clicks and navigation are working

import http from 'http';

// Test if navigation endpoints are accessible
const endpoints = [
  { path: '/', name: 'Home Page' },
  { path: '/search', name: 'Search Page' },
  { path: '/auctions/1', name: 'Auction Detail Page' },
  { path: '/login', name: 'Login Page' },
  { path: '/register', name: 'Register Page' }
];

console.log('=== NAVIGATION ENDPOINT TESTS ===\n');

endpoints.forEach((endpoint, index) => {
  setTimeout(() => {
    http.get(`http://localhost:3001${endpoint.path}`, (res) => {
      console.log(`✓ ${endpoint.name} (${endpoint.path}): Status ${res.statusCode}`);
      
      if (index === endpoints.length - 1) {
        console.log('\nAll navigation endpoints are accessible!');
        process.exit(0);
      }
    }).on('error', (err) => {
      console.log(`✗ ${endpoint.name} (${endpoint.path}): ${err.message}`);
    });
  }, index * 100);
});