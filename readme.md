# Scelte di sviluppo e progettazione
- Versione di java: JAVA 22
- Struttura del progetto: MAVEN
  - Libreria javaluator
- IDE: InteliJ IDEA

## Tcp Server
Per accettare le connessioni TCP il programma:
1. Inizializza java.net.ServerSocket per accettare le connessioni in arrivo sulla porta p
2. Crea il ServerSocket, ma su un thread separato per non bloccare l’esecuzione del codice
3. Un thread viene infine creato per ogni connessione TCP che viene stabilita, in questo modo il programma può avere più di una connessione aperta.

## Evaluation di una stringa

Per accettare le connessioni TCP il programma:
Inizializza java.net.ServerSocket per accettare le connessioni in arrivo sulla porta `p`. 

Crea il ServerSocket, ma su un thread separato per non bloccare l’esecuzione del codice
Un thread viene infine creato per ogni connessione TCP che viene stabilita, in questo modo il programma può avere più di una connessione aperta.

`2+4+5 → (2+4)+5 → ((2+4)+5)`

Dopo che gli operatori sono incapsulati, si esegue l’algoritmo del “Parse Tree”, il quale costruisce la struttura ad albero.
- Inizializza il primo nodo, detto radice, poi per ogni elemento della stringa matematica esegui:
  1.	Se incontri un’apertura di parentesi ‘(’, crea un nuovo nodo sinistro e muoviti alla sua posizione.
  2.	Se incontri un operatore ‘^’ ‘*’ /‘’ ‘-’ ‘+’, impostalo come valore del nodo attuale. Al termine crea un nuovo nodo destro e muoviti alla sua posizione.
  3.	Se incontri una costante o un numero, impostalo come valore del nodo e muoviti al tuo genitore.
  4.	Se incontri una chiusura di parentesi ‘)’, muoviti al tuo genitore.

Con la struttura ad albero binario creata, possiamo facilmente calcolare il risultato semplificando di mano in mano i nodi.

## Gestione della computazione
### Approccio sincrono:
Se per calcolare un risultato ci metto 1 secondo, durante quel tempo non è possibile leggere o inviare altri messaggi, in quanto il thread è impegnato. Per esempio, se volessi inviare il comando BYE quando è in corso un calcolo, la connessione verrebbe chiusa solo dopo che il risultato è stato trovato.
### Approccio su Threads:
Se si utilizzassero dei thread, ci sarebbe il rischio che l’ordine di input dei comandi non combaci con l’ordine di output, dato che una computazione potrebbe essere più veloce di un’altra.
### Approccio ExecutorService:
Permette di gestire task asincrone utilizzando un numero massimo di thread paralleli. Questo è l’approccio migliore per il progetto, in quanto permette di continuare a gestire l’I/O del socket con le computazioni eseguite in ordine su un Thread separato.
