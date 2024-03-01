#!/usr/bin/env python
"""Assignment 4 Part 3"""
print(__doc__)

import random
from collections import namedtuple
from typing import NamedTuple
from typing import IO


Range = namedtuple("Range", "min max")

class CanvasSize(NamedTuple):
    """CanvasSize class"""
    width: int
    height: int


# NamedTuple classes below hold ranges we will randomly generate from
# if you want to change ranges, change values in below classes

class Colour(NamedTuple):
    """Colour class"""
    red: Range = Range(220,255)
    green: Range = Range(220,255)
    blue: Range = Range(120,150)
    opacity: Range = Range(0.3, 0.9)


class Coordinate(NamedTuple):
    """Coordinate class"""
    x: Range = Range(0,1000)
    y: Range = Range(0,600)


class Radii(NamedTuple):
    """Radii class"""
    circle: Range = Range(0,100)
    ellipse_width: Range = Range(10,30)
    ellipse_height: Range = Range(10,30)


class RectDimension(NamedTuple):
    """RectDimension class"""
    width: Range = Range(10,100)
    height: Range = Range(10,100)

#end of range classes

class PyArtConfig:
    """PyArtConfig class"""
    DEFAULT_SHAPE_COUNTER: int = 700
    DEFAULT_SHAPE: Range = Range(2, 2)
    DEFAULT_COORDINATE: Coordinate = Coordinate()
    DEFAULT_RADII: Radii = Radii()
    DEFAULT_RECTDIMENSION: RectDimension = RectDimension()
    DEFAULT_COLOUR: Colour = Colour()

    
    def __init__(self, counter = DEFAULT_SHAPE_COUNTER, shape = DEFAULT_SHAPE, coordinate = DEFAULT_COORDINATE, radii = DEFAULT_RADII,
                 rect_dimension = DEFAULT_RECTDIMENSION, colour = DEFAULT_COLOUR):
        """__init__ method"""
        self.counter: int = counter
        self.shape: Range = shape
        self.coordinate: Coordinate = coordinate
        self.radii: Radii = radii
        self.rect_dimension: RectDimension = rect_dimension
        self.colour: Colour = colour

        self.shapes: list = []

        self.generate_shapes()


    def generate_shapes(self):
        """generate_shapes method"""
        for index in range(0, self.counter):
            cur_shape: RandomShape = RandomShape(self.shape, self.coordinate, self.radii, self.rect_dimension, self.colour)
            self.shapes.append(cur_shape)

    def print_shapes(self):
        """print_shapes method"""
        print(f'CNT SHA   X   Y RAD  RX  RY   W   H   R   G   B  OP')
        for index, shape in enumerate(self.shapes):
            print(f'{index:3} {shape.as_Part2_line()}')


class RandomShape:
    """RandomShape class"""
    def __init__(self, shape, coordinate, radii, rect_dimension, colour):
        """__init__ method"""
        self.shape_range: Range = shape
        self.coordinate: Coordinate = coordinate
        self.radii: Radii = radii
        self.rect_dimension: RectDimension = rect_dimension
        self.colour: Colour = colour
        self.generate_random_values()

    def generate_random_values(self):
        """generate_random_values method"""
        self.shape: int = random.randint(self.shape_range.min, self.shape_range.max)
        self.x: int = random.randint(self.coordinate.x.min, self.coordinate.x.max)
        self.y: int = random.randint(self.coordinate.y.min, self.coordinate.y.max)
        self.cir_rad: int = random.randint(self.radii.circle.min, self.radii.circle.max)
        self.rad_x: int = random.randint(self.radii.ellipse_width.min, self.radii.ellipse_width.max)
        self.rad_y: int = random.randint(self.radii.ellipse_height.min, self.radii.ellipse_height.max)
        self.width: int = random.randint(self.rect_dimension.width.min, self.rect_dimension.width.max)
        self.height: int = random.randint(self.rect_dimension.height.min, self.rect_dimension.height.max)
        self.red: int = random.randint(self.colour.red.min, self.colour.red.max)
        self.green: int = random.randint(self.colour.green.min, self.colour.green.max)
        self.blue: int = random.randint(self.colour.blue.min, self.colour.blue.max)
        self.opacity: float = round(random.uniform(self.colour.opacity.min, self.colour.opacity.max), 1)

    def __str__(self) -> str:
        """__str__ method"""
        if self.shape == 0:
            circle: CircleShape = CircleShape(self)
            return circle.__str__()
        elif self.shape == 1:
            rectangle: RectangleShape = RectangleShape(self)
            return rectangle.__str__()
        else:
            ellipse: EllipseShape = EllipseShape(self)
            return ellipse.__str__()

    def as_Part2_line(self) -> str:
        """as_Part2_line method"""
        return (f"{self.shape:3} {self.x:3} {self.y:3} {self.cir_rad:3} {self.rad_x:3} {self.rad_y:3} {self.width:3} {self.height:3} {self.red:3} {self.green:3} {self.blue:3} {self.opacity:3}")

    def as_svg(self):
        """as_svg method"""
        if self.shape == 0:
            circle: CircleShape = CircleShape(self)
            return circle.as_svg()
        elif self.shape == 1:
            rectangle: RectangleShape = RectangleShape(self)
            return rectangle.as_svg()
        else:
            ellipse: EllipseShape = EllipseShape(self)
            return ellipse.as_svg()


class CircleShape:
    """CircleShape class"""
    def __init__(self, parameters: RandomShape) -> None:
        """__init__ method"""
        self.parameters: RandomShape = parameters

    def __str__(self) -> str:
        """__str__ method"""
        multi_line: str = "Generated random circle\n"
        multi_line += f'radius: {self.parameters.cir_rad}\n'
        multi_line += f'center(x,y): ({self.parameters.x},{self.parameters.y})\n'
        multi_line += f'colour(red,green,blue): ({self.parameters.red},{self.parameters.green},{self.parameters.blue})\n'
        multi_line += f'opacity: {self.parameters.opacity}\n'
        return multi_line
    
    def as_svg(self) -> str:
        """as_svg method"""
        #returns svg code as str
        return f'<circle cx="{self.parameters.x}" cy="{self.parameters.y}" r="{self.parameters.cir_rad}" fill="rgb({self.parameters.red}, {self.parameters.green}, {self.parameters.blue})" fill-opacity="{self.parameters.opacity}"></circle>\n'


class RectangleShape:
    """RectangleShape class"""
    def __init__(self, parameters: RandomShape) -> None:
        """__init__ method"""
        self.parameters: RandomShape = parameters

    def __str__(self) -> str:
        """__str__ method"""
        multi_line: str = "Generated random rectangle\n"
        multi_line += f'width: {self.parameters.width}\n'
        multi_line += f'height: {self.parameters.height}\n'
        multi_line += f'colour(red,green,blue): ({self.parameters.red},{self.parameters.green},{self.parameters.blue})\n'
        multi_line += f'opacity: {self.parameters.opacity}\n'
        return multi_line
    
    def as_svg(self) -> str:
        """as_svg method"""
        #returns svg code as str
        return f'<rect x="{self.parameters.x}" y="{self.parameters.y}" width="{self.parameters.width}" height="{self.parameters.height}" fill="rgb({self.parameters.red}, {self.parameters.green}, {self.parameters.blue})" fill-opacity="{self.parameters.opacity}"></rect>\n'


class EllipseShape:
    """EllipseShape class"""
    def __init__(self, parameters: RandomShape) -> None:
        """__init__ method"""
        self.parameters: RandomShape = parameters

    def __str__(self) -> str:
        """__str__ method"""
        multi_line: str = "Generated random ellipse\n"
        multi_line += f'radius(width,height): ({self.parameters.rad_x},{self.parameters.rad_y})\n'
        multi_line += f'colour(red,green,blue): ({self.parameters.red},{self.parameters.green},{self.parameters.blue})\n'
        multi_line += f'opacity: {self.parameters.opacity}\n'
        return multi_line
    
    def as_svg(self) -> str:
        """as_svg method"""
        #returns svg code as str
        return f'<ellipse cx="{self.parameters.x}" cy="{self.parameters.y}" rx="{self.parameters.rad_x}" ry="{self.parameters.rad_y}" fill="rgb({self.parameters.red}, {self.parameters.green}, {self.parameters.blue})" fill-opacity="{self.parameters.opacity}"></ellipse>\n'


class Circle:
    """Circle class"""
    def __init__(self, cir: tuple, col: tuple) -> None:
        """__init__ method"""
        self.cx: int = cir[0]
        self.cy: int = cir[1]
        self.rad: int = cir[2]
        self.red: int = col[0]
        self.green: int = col[1]
        self.blue: int = col[2]
        self.op: float = col[3]


class HtmlComponent:
    """HtmlComponent class"""
    def __init__(self, file: str):
        """__init__ method"""
        self.file = file

    def write_code(self, tab: int, code: str) -> None:
        """write_code method"""
        tab_str: str = "   " * tab
        self.file.write(f"{tab_str}{code}")


class HtmlDocument(HtmlComponent):
    """HtmlDocument class"""
    def __init__(self, title, file):
        """__init__ method"""
        self.title: str = title
        self.file: IO[str] = file

    def add_line(self, tab: int, line:str):
        """add_line method"""
        super().write_code(tab, line)

    def generateHTMLstart(self):
        """generateHTMLstart method"""
        super().write_code(0, f"<!DOCTYPE html>\n<html>\n<head>\n  <title>{self.title}</title>\n</head>\n<body>\n")

    def generateHTMLclose(self):
        """generateHTMLclose method"""
        super().write_code(0, "</body>\n</html>")


class SVGCanvas:
    """SVGCanvas class"""
    def __init__(self, tab: int, canvas: CanvasSize, html: HtmlDocument):
        """__init__ method"""
        self.tab: int = tab
        self.canvas: CanvasSize = canvas
        self.htmlDoc: HtmlDocument = html
        
    def openSVGcanvas(self) -> None:
        """openSVGcanvas method"""
        self.htmlDoc.add_line(self.tab, '<!--Define SVG drawing box-->\n')
        self.htmlDoc.add_line(self.tab, f'<svg width="{self.canvas.width}" height="{self.canvas.height}">\n')

    def closeSVGcanvas(self) -> None:
        """closeSVGcanvas method"""
        self.htmlDoc.add_line(self.tab, '</svg>\n')

    def generate_shapes(self, shapes: PyArtConfig):
        """generate_shapes method"""
        for shape in shapes.shapes:
            self.htmlDoc.add_line(2, shape.as_svg())


def create_PyArt() -> None:
    """create_PyArt method"""
    fnam: str = "Parsons-A4-P3-5.html"
    winTitle: str = "My Art"
    file: IO[str] = open(fnam, "w")

    doc = HtmlDocument(winTitle, file)
    doc.generateHTMLstart()

    svg: SVGCanvas = SVGCanvas(1, CanvasSize(1000, 600), doc)

    svg.openSVGcanvas()

    pyArt: PyArtConfig = PyArtConfig()
    svg.generate_shapes(pyArt)

    svg.closeSVGcanvas()

    doc.generateHTMLclose()
    file.close()


def main() -> None:
    """main method"""
    create_PyArt()

if __name__ == "__main__":
    main()