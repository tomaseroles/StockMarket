# StockMarket

StockMarket es un juego multijugador en tiempo real, con entorno gráfico, desarrollado en Java que simula operaciones de compra-venta de acciones en bolsa.
El programa utiliza datos de entrada que provienen de un servicio proporcionado por la empresa [iextrading](https://iextrading.com/developer/docs/) con una API muy potente que proporciona gran cantidad de datos, de los cuales algunos se han usado en la aplicación.

El juego utiliza una base de datos hospedada en un servidor externo en el que se almacenan datos acerca de los jugadores, las empresas que cotizan en bolsa y las transacciones de los jugadores con esas acciones.

Los datos de las empresas se obtienen de la API, y los jugadores y las transacciones, mediante el programa al dar de alta a los usuarios y sus transacciones.
Los usuarios del juego están organizados en tres categorías: administrador, jugador e invitado.

La información inicialmente disponible en la BBDD contiene los símbolos y nombres de las empresas, que pueden ser habilitados o suspendidos para cotización por el administrador.
El usuario invitado (o guest) puede acceder a la aplicación sin autenticación y sólo puede acceder a la lista de empresas habilitadas y al ranking de jugadores.
Los jugadores pueden realizar operaciones de compra-venta de acciones y acceder a la información disponible de las empresas.

