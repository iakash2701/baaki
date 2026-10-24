/**
 * auth_security.test.js
 * Validates isolation boundaries across multiple user accounts and data contexts.
 */

import assert from 'node:assert';
import { getStorageKeys, seedDemoDataIfEmpty, loadItem, saveItem } from '../data/storage.js';

// Setup Mock LocalStorage for Node test execution
const storageMock = (() => {
  let store = {};
  return {
    getItem: (key) => store[key] || null,
    setItem: (key, value) => { store[key] = String(value); },
    removeItem: (key) => { delete store[key]; },
    clear: () => { store = {}; }
  };
})();

global.localStorage = storageMock;

console.log('--- RUNNING MULTI-USER DATA ISOLATION TESTS ---');

// Test 1: User Key Prefixing & Separation
const userAId = 'usr_alice_123';
const userBId = 'usr_bob_456';

const keysA = getStorageKeys(userAId);
const keysB = getStorageKeys(userBId);

assert.notStrictEqual(keysA.CUSTOMERS, keysB.CUSTOMERS, 'User A and User B customer keys must not match');
assert.notStrictEqual(keysA.INVOICES, keysB.INVOICES, 'User A and User B invoice keys must not match');
console.log('✓ Storage keys correctly partitioned per user identity');

// Test 2: Seed Isolation
seedDemoDataIfEmpty(userAId);

const custsA = loadItem(keysA.CUSTOMERS, []);
assert.strictEqual(custsA.length > 0, true, 'User A should have seeded customers');
custsA.forEach(c => {
  assert.strictEqual(c.userId, userAId, `Customer ${c.id} must belong to User A`);
});

const custsBInitial = loadItem(keysB.CUSTOMERS, []);
assert.strictEqual(custsBInitial.length, 0, 'User B workspace must be empty before seeding');
console.log('✓ User data isolation verified: User A seed does not leak into User B');

// Test 3: Mutations on User A do not affect User B
saveItem(keysA.CUSTOMERS, [{ id: 'cust_secret_a', userId: userAId, name: 'Secret A' }]);
seedDemoDataIfEmpty(userBId);

const custsAAfter = loadItem(keysA.CUSTOMERS, []);
const custsBAfter = loadItem(keysB.CUSTOMERS, []);

assert.strictEqual(custsAAfter.length, 1, 'User A should retain modified records');
assert.strictEqual(custsAAfter[0].name, 'Secret A');
assert.strictEqual(custsBAfter.some(c => c.id === 'cust_secret_a'), false, 'User B must not see User A records');
console.log('✓ Cross-user modification leakage prevented');

console.log('--- ALL MULTI-USER ISOLATION TESTS PASSED ---');
