class Node {
    constructor(data) {
        this.data = data;
        this.h = 1;
        this.left = null;
        this.right = null;
    }
}

let root = null;
let sz = 0;


/* =========================
   AVL TREE LOGIC
   ========================= */

function updateHeight(root) {
    root.h = 1 + Math.max(
        root.left === null ? 0 : root.left.h,
        root.right === null ? 0 : root.right.h
    );
}

function getBF(root) {
    return (root.left === null ? 0 : root.left.h) -
           (root.right === null ? 0 : root.right.h);
}

function rightRotate(root) {
    let temp = root.left;

    root.left = temp.right;
    temp.right = root;

    updateHeight(root);
    updateHeight(temp);

    return temp;
}

function leftRotate(root) {
    let temp = root.right;

    root.right = temp.left;
    temp.left = root;

    updateHeight(root);
    updateHeight(temp);

    return temp;
}

function insertH(root, x) {
    if (root === null)
        return new Node(x);

    if (x < root.data)
        root.left = insertH(root.left, x);
    else
        root.right = insertH(root.right, x);

    updateHeight(root);

    if (getBF(root) === -2) {
        if (getBF(root.right) === -1) {
            // RR
            root = leftRotate(root);
        }
        else {
            // RL
            root.right = rightRotate(root.right);
            root = leftRotate(root);
        }
    }
    else if (getBF(root) === 2) {
        if (getBF(root.left) === 1) {
            // LL
            root = rightRotate(root);
        }
        else {
            // LR
            root.left = leftRotate(root.left);
            root = rightRotate(root);
        }
    }

    return root;
}

function insert(x) {
    root = insertH(root, x);
    sz++;
}


/* =========================
   TREE SIZE CALCULATION
   ========================= */

function getTreeWidth(node) {
    if (node === null) {
        return 0;
    }

    if (node.left === null && node.right === null) {
        return 1;
    }

    return getTreeWidth(node.left) + getTreeWidth(node.right);
}

function getTreeHeight(node) {
    if (node === null) {
        return 0;
    }

    return 1 + Math.max(
        getTreeHeight(node.left),
        getTreeHeight(node.right)
    );
}


/* =========================
   DRAW AVL TREE
   ========================= */

function renderTree() {
    const treeContainer = document.getElementById("treeContainer");

    treeContainer.innerHTML = "";

    if (root === null) {
        treeContainer.innerHTML =
            '<p class="empty-message">Insert a value to create the AVL tree</p>';
        return;
    }

    const width = Math.max(700, getTreeWidth(root) * 110);
    const height = Math.max(350, getTreeHeight(root) * 110);

    const svg = document.createElementNS(
        "http://www.w3.org/2000/svg",
        "svg"
    );

    svg.setAttribute("width", width);
    svg.setAttribute("height", height);
    svg.setAttribute("viewBox", `0 0 ${width} ${height}`);
    svg.classList.add("avl-svg");

    const positions = new Map();

    calculatePositions(
        root,
        width / 2,
        50,
        width / 4,
        positions
    );

    drawEdges(root, svg, positions);
    drawNodes(root, svg, positions);

    treeContainer.appendChild(svg);
}


/* =========================
   CALCULATE NODE POSITIONS
   ========================= */

function calculatePositions(node, x, y, horizontalGap, positions) {
    if (node === null) {
        return;
    }

    positions.set(node, {
        x: x,
        y: y
    });

    if (node.left !== null) {
        calculatePositions(
            node.left,
            x - horizontalGap,
            y + 100,
            horizontalGap / 2,
            positions
        );
    }

    if (node.right !== null) {
        calculatePositions(
            node.right,
            x + horizontalGap,
            y + 100,
            horizontalGap / 2,
            positions
        );
    }
}


/* =========================
   DRAW CONNECTION LINES
   ========================= */

function drawEdges(node, svg, positions) {
    if (node === null) {
        return;
    }

    const parentPosition = positions.get(node);

    if (node.left !== null) {
        const childPosition = positions.get(node.left);

        createLine(
            svg,
            parentPosition.x,
            parentPosition.y,
            childPosition.x,
            childPosition.y
        );

        drawEdges(node.left, svg, positions);
    }

    if (node.right !== null) {
        const childPosition = positions.get(node.right);

        createLine(
            svg,
            parentPosition.x,
            parentPosition.y,
            childPosition.x,
            childPosition.y
        );

        drawEdges(node.right, svg, positions);
    }
}

function createLine(svg, x1, y1, x2, y2) {
    const line = document.createElementNS(
        "http://www.w3.org/2000/svg",
        "line"
    );

    line.setAttribute("x1", x1);
    line.setAttribute("y1", y1);
    line.setAttribute("x2", x2);
    line.setAttribute("y2", y2);

    line.classList.add("tree-line");

    svg.appendChild(line);
}


/* =========================
   DRAW NODES
   ========================= */

function drawNodes(node, svg, positions) {
    if (node === null) {
        return;
    }

    const position = positions.get(node);

    createNode(
        svg,
        position.x,
        position.y,
        node.data
    );

    drawNodes(node.left, svg, positions);
    drawNodes(node.right, svg, positions);
}

function createNode(svg, x, y, value) {
    const group = document.createElementNS(
        "http://www.w3.org/2000/svg",
        "g"
    );

    const circle = document.createElementNS(
        "http://www.w3.org/2000/svg",
        "circle"
    );

    circle.setAttribute("cx", x);
    circle.setAttribute("cy", y);
    circle.setAttribute("r", 27);

    circle.classList.add("tree-node");

    const text = document.createElementNS(
        "http://www.w3.org/2000/svg",
        "text"
    );

    text.setAttribute("x", x);
    text.setAttribute("y", y + 6);

    text.setAttribute("text-anchor", "middle");

    text.classList.add("tree-node-text");

    text.textContent = value;

    group.appendChild(circle);
    group.appendChild(text);

    svg.appendChild(group);
}


/* =========================
   BUTTON CONTROLS
   ========================= */

const valueInput = document.getElementById("valueInput");
const insertBtn = document.getElementById("insertBtn");
const resetBtn = document.getElementById("resetBtn");

const operation = document.getElementById("operation");
const rootValue = document.getElementById("rootValue");
const nodeCount = document.getElementById("nodeCount");


insertBtn.addEventListener("click", function () {
    const value = Number(valueInput.value);

    if (valueInput.value === "") {
        return;
    }

    insert(value);

    operation.textContent = "Inserted " + value;
    rootValue.textContent = root.data;
    nodeCount.textContent = sz;

    valueInput.value = "";
    valueInput.focus();

    renderTree();
});


resetBtn.addEventListener("click", function () {
    root = null;
    sz = 0;

    operation.textContent = "None";
    rootValue.textContent = "-";
    nodeCount.textContent = "0";

    renderTree();
});


valueInput.addEventListener("keydown", function (event) {
    if (event.key === "Enter") {
        insertBtn.click();
    }
});