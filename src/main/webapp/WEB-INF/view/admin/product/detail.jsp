<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>


            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Product detail</title>

                <!-- Latest compiled and minified CSS -->
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

                <!-- Latest compiled JavaScript -->
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

                <link href="/css/styles.css" rel="stylesheet" />
                <script src="https://use.fontawesome.com/releases/v6.3.0/js/all.js" crossorigin="anonymous"></script>
            </head>

            <body class="sb-nav-fixed">
                <jsp:include page="../layout/header.jsp" />

                <div id="layoutSidenav">
                    <jsp:include page="../layout/sidebar.jsp" />

                    <div id="layoutSidenav_content">
                        <main>
                            <div class="container-fluid px-4">
                                <h1 class="mt-4">Product</h1>

                                <ol class="breadcrumb mb-4">
                                    <li class="breadcrumb-item"><a href="/admin">Dashboard</a></li>
                                    <li class="breadcrumb-item"><a href="/admin/product">Product</a></li>
                                    <li class="breadcrumb-item active">View</li>
                                </ol>
                            </div>

                            <div class="container mt-5">
                                <div class="row">
                                    <div class="col-12 mx-auto">
                                        <div class="d-flex justify-content-between">
                                            <h3>Product detail</h3>
                                        </div>

                                        <hr>

                                        <div class="card" style="width: 50%;">
                                            <div class="card-header">
                                                Product info
                                            </div>
                                            <ul class="list-group list-group-flush">
                                                <li class="list-group-item">ID: ${dataProduct.id}</li>
                                                <li class="list-group-item">Name: ${dataProduct.name}</li>
                                                <li class="list-group-item">Price: ${dataProduct.price}</li>
                                                <li class="list-group-item">Quantity: ${dataProduct.quantity}</li>
                                                <li class="list-group-item">Sold: ${dataProduct.sold}</li>
                                                <li class="list-group-item">Detail desc: ${dataProduct.detailDesc}</li>
                                                <li class="list-group-item">Short desc: ${dataProduct.shortDesc}</li>
                                                <li class="list-group-item">Factory: ${dataProduct.factory}</li>
                                                <li class="list-group-item">Target: ${dataProduct.target}</li>
                                            </ul>
                                            <img class="card-img-top mt-5" src="/img/product/${dataProduct.image}"
                                                alt="">
                                        </div>

                                        <a href="/admin/product" class="btn btn-success">Back</a>
                                    </div>
                                </div>
                            </div>
                        </main>

                        <jsp:include page="../layout/footer.jsp" />
                    </div>
                </div>

                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
                    crossorigin="anonymous"></script>
                <script src="js/scripts.js"></script>
            </body>

            </html>