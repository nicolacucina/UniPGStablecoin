async function createCoinChart() {
    d3.csv("./simulation/bin/data/run1.csv", (d) => {
        return {
            day: +d.Day,
            coin: +d.Price,
        };
    }).then((data) => {
        drawCoinLineChart(data);
    });
    
    const drawCoinLineChart = (data) => {
        
        const margin = { top: 40, right: 100, bottom: 25, left: 40 };
        const width = 500;
        const height = 400;
        const innerWidth = width - (margin.left + margin.right);
        const innerHeight = height - (margin.top + margin.bottom);
    
        const svg = d3
        .select("#coin-line-chart")
        .append("svg")
        .attr("viewBox", `0 0 ${width} ${height}`);
    
        const innerChart = svg
        .append("g")
        .attr("transform", `translate(${margin.left}, ${margin.top})`)

        const xScale = d3
            .scaleLinear()
            .domain([0, d3.max(data, (d) => d.day)])
            .range([0, innerWidth]);
        
        const yScale = d3
            .scaleLinear()
            .domain([0, d3.max(data, (d) => d.coin)])
            .range([innerHeight, 0]);
    
        const bottomAxis = d3
            .axisBottom(xScale)

        innerChart
            .append("g")
            .attr("class", "axis-x")
            .attr("transform", `translate(0, ${innerHeight})`)
            .call(bottomAxis);
    
        const leftAxis = d3.axisLeft(yScale);
        innerChart.append("g").attr("class", "axis-y").call(leftAxis);
    
        d3.selectAll(".axis-x text, .axis-y text")
            .style("font-family", "Roboto, sans-serif")
            .style("font-size", "14px");
    
        d3.selectAll(".axis-y text")
            .attr("x", "-10px");

        // Y-Label
        svg
            .append("text")
            .text("Price (€)")
            .attr("y", 30)
            .attr("font-family", "sans-serif");
        
        // X-Label
        svg
            .append("text")
            .text("Day")
            .attr("x", width - margin.right)
            .attr("y", height - margin.bottom + 20)
            .attr("font-family", "sans-serif");
    
        // Line Chart 
        const violet = "#8080ff";
        innerChart
            .selectAll("circle")
            .data(data)
            .join("circle")
            .attr("r", 4)
            .attr("cx", (d) => xScale(d.day))
            .attr("cy", (d) => yScale(d.coin))
            .attr("fill", violet);
    
        const curveGenerator = d3
            .line()
            .x((d) => xScale(d.day))
            .y((d) => yScale(d.coin))
            .curve(d3.curveCatmullRom);
    
        innerChart
            .append("path")
            .attr("d", curveGenerator(data))
            .attr("fill", "none")
            .attr("stroke", violet);

        // Equilibrium Line
        const lineGenerator = d3
            .line()
            .x((d) => xScale(d.day))
            .y((d) => yScale(1.0));

        innerChart
            .append("path")
            .attr("class", "line")
            .style("stroke-dasharray", ("3, 3"))
            .attr("d", lineGenerator(data))
            .attr("fill", "none")
            .attr("stroke", violet);

        // Equilibrium Label
        svg
            .append("text")
            .text("Equilibrium Price")
            .attr("x", width - margin.right)
            .attr("y", yScale(1.0) + 60)
            .attr("font-family", "sans-serif")
            .attr("font-size", "12px")
            .attr("fill", violet);
    };
}    