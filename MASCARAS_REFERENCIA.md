# 📋 REFERÊNCIA DE MÁSCARAS - Desafio Backend SEA

## ✅ Regras de Persistência e Exibição

### **CPF**
- **Persistir:** COM máscara (14 caracteres)
- **Exibir:** COM máscara
- **Formato Banco:** `123.456.789-00`
- **Formato Tela:** `123.456.789-00`
- **Length Entity:** `@Column(length = 14)`

---

### **CEP**
- **Persistir:** SEM máscara (8 dígitos)
- **Exibir:** COM máscara
- **Formato Banco:** `12345678`
- **Formato Tela:** `12345-678`
- **Length Entity:** `@Column(length = 8)`

---

### **Telefone**
- **Persistir:** SEM máscara (máximo 11 dígitos)
- **Exibir:** COM máscara (varia por tipo)
- **Formato Banco:** `11987654321` (apenas números)
- **Formato Tela:**
  - Residencial/Comercial: `(11) 3456-7890` (10 dígitos)
  - Celular: `(11) 98765-4321` (11 dígitos)
- **Length Entity:** `@Column(length = 11)`

---

## 🔧 Métodos Utilitários (Implementar nos Services)

### **CPF:**
```java
// CPF já vem com máscara, apenas valida
public boolean validarFormatoCPF(String cpf) {
    return cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
}
```

### **CEP:**
```java
// Remove máscara antes de salvar
public String removerMascaraCEP(String cep) {
    return cep.replaceAll("[^0-9]", ""); // Remove tudo que não é número
}

// Aplica máscara para exibir
public String aplicarMascaraCEP(String cep) {
    return cep.replaceAll("(\\d{5})(\\d{3})", "$1-$2"); // 12345678 → 12345-678
}
```

### **Telefone:**
```java
// Remove máscara antes de salvar
public String removerMascaraTelefone(String telefone) {
    return telefone.replaceAll("[^0-9]", "");
}

// Aplica máscara para exibir (depende do tipo)
public String aplicarMascaraTelefone(String telefone, TipoTelefone tipo) {
    String limpo = removerMascaraTelefone(telefone);
    
    if (tipo == TipoTelefone.CELULAR && limpo.length() == 11) {
        // (11) 98765-4321
        return limpo.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
    } else if (limpo.length() == 10) {
        // (11) 3456-7890
        return limpo.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
    }
    
    return telefone; // Retorna original se não couber no padrão
}
```

---

## 📊 Tabela Resumo

| Campo | Banco (Persistir) | Tela (Exibir) | Length | Exemplo Banco | Exemplo Tela |
|-------|-------------------|---------------|--------|---------------|--------------|
| CPF | COM máscara | COM máscara | 14 | 123.456.789-00 | 123.456.789-00 |
| CEP | SEM máscara | COM máscara | 8 | 12345678 | 12345-678 |
| Telefone Residencial | SEM máscara | COM máscara | 11 | 1134567890 | (11) 3456-7890 |
| Telefone Celular | SEM máscara | COM máscara | 11 | 11987654321 | (11) 98765-4321 |

---

## ✅ Status das Entidades

- ✅ **Cliente.cpf:** `@Column(length = 14)` - CORRETO
- ✅ **Endereco.cep:** `@Column(length = 8)` - CORRIGIDO
- ✅ **Telefone.numero:** `@Column(length = 11)` - CORRIGIDO

---

## 🎯 Próximos Passos

1. Implementar métodos utilitários em classe `MascaraUtils` ou diretamente nos Services
2. EnderecoService: Usar `removerMascaraCEP()` antes de salvar
3. TelefoneService: Usar `removerMascaraTelefone()` antes de salvar
4. DTOs: Aplicar máscaras ao retornar dados para frontend
