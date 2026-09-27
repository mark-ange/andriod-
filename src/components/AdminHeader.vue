<template>
  <header class="header">

    <div class="search-box">
      <span class="search-icon">⌕</span>
      <input
        type="text"
        placeholder="Search areas, tickets, or personnel..."
      />
    </div>

    <div class="header-actions">
      <button class="header-button">
        ♧
      </button>

      <div class="avatar">
        AD
      </div>
    </div>

  </header>
</template>

<style scoped>
.header {
  height: 58px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.search-box {
  width: 300px;
  height: 34px;

  display: flex;
  align-items: center;

  border: 1px solid #dfe4ea;
  border-radius: 5px;

  background: #ffffff;
}

.search-icon {
  padding-left: 10px;
  color: #98a2b3;
  font-size: 18px;
}

.search-box input {
  flex: 1;
  height: 100%;

  border: none;
  outline: none;

  padding: 0 8px;

  font-size: 11px;
  color: #344054;
  background: transparent;
}

.search-box input::placeholder {
  color: #98a2b3;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 14px;
}

.header-button {
  border: none;
  background: transparent;

  color: #667085;
  font-size: 19px;

  cursor: pointer;
}

.avatar {
  width: 30px;
  height: 30px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 50%;

  background: #278c37;
  color: white;

  font-size: 10px;
  font-weight: 700;
}
</style>