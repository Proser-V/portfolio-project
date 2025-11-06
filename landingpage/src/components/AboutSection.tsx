import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Github, Linkedin, ExternalLink } from "lucide-react";

const teamMembers = [
  {
    name: "Valentin DUMONT",
    role: "Full-Stack Developer",
    linkedin: "https://www.linkedin.com/in/vtn-dumont/",
    github: "https://github.com/Proser-V",
  },
  {
    name: "Quentin LATASTE",
    role: "Full-Stack Developer",
    linkedin: "https://www.linkedin.com/in/quentin-lataste-b5a359193/",
    github: "https://github.com/loufi84",
  },
];

export const AboutSection = () => {
  return (
    <section id="about" className="py-20">
      <div className="container px-4">
        <div className="max-w-4xl mx-auto">
          <h2 className="text-3xl md:text-4xl font-bold mb-8 text-center">
            À propos du projet
          </h2>

          <Card className="mb-12">
            <CardContent className="p-8">
              <h3 className="text-2xl font-semibold mb-4">Notre inspiration</h3>
              <div className="space-y-4 text-muted-foreground">
                <p>
                  L'idée de L'Atelier Local est née d'un constat simple :
                  les artisans locaux, porteurs de savoir-faire et de passion,
                  restent souvent peu visibles en ligne.
                  Notre objectif est de créer un lien direct, humain et durable entre eux et leurs clients.
                </p>
                <p>
                  Nous avons voulu créer une plateforme qui ne se contente pas de faciliter la mise en relation,
                  mais qui valorise aussi le travail manuel, l'expertise locale et les échanges authentiques entre les personnes.
                  Un espace où chaque artisan peut partager ses réalisations,
                  raconter son histoire, et où chacun peut découvrir facilement le savoir-faire qui l'entoure.
                </p>
                <p>
                  Ce projet a été imaginé et développé dans le cadre du programme de la Holberton School,
                  en tant que projet de portfolio. Il incarne notre vision d'une économie locale plus connectée,
                  plus humaine et plus accessible à tous.
                </p>
                <div className="flex gap-4 mt-6">
                  <Button variant="outline" asChild>
                    <a 
                      href="https://www.holbertonschool.com" 
                      target="_blank" 
                      rel="noopener noreferrer"
                      className="flex items-center gap-2"
                    >
                      Holberton School <ExternalLink className="h-4 w-4" />
                    </a>
                  </Button>
                  <Button variant="outline" asChild>
                    <a 
                      href="https://github.com/Proser-V/portfolio-project" 
                      target="_blank" 
                      rel="noopener noreferrer"
                      className="flex items-center gap-2"
                    >
                      Voir le code <Github className="h-4 w-4" />
                    </a>
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>

          <div>
            <h3 className="text-2xl font-semibold mb-6 text-center">L'équipe</h3>
            <div className="grid md:grid-cols-2 lg:grid-cols-2 gap-6">
              {teamMembers.map((member, index) => (
                <Card key={index}>
                  <CardContent className="p-6 text-center">
                    <h4 className="text-xl font-semibold mb-2">{member.name}</h4>
                    <p className="text-muted-foreground mb-4">{member.role}</p>
                    <div className="flex justify-center gap-3">
                      <Button size="icon" variant="ghost" asChild>
                        <a 
                          href={member.linkedin} 
                          target="_blank" 
                          rel="noopener noreferrer"
                          aria-label="LinkedIn"
                        >
                          <Linkedin className="h-5 w-5" />
                        </a>
                      </Button>
                      <Button size="icon" variant="ghost" asChild>
                        <a 
                          href={member.github} 
                          target="_blank" 
                          rel="noopener noreferrer"
                          aria-label="GitHub"
                        >
                          <Github className="h-5 w-5" />
                        </a>
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
